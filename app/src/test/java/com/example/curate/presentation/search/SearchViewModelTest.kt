package com.example.curate.presentation.search

import androidx.paging.PagingData
import com.example.curate.domain.model.RecentSearch
import com.example.curate.domain.model.Wallpaper
import com.example.curate.domain.repository.SearchHistoryRepository
import com.example.curate.domain.repository.WallpaperRepository
import com.example.curate.domain.usecase.ClearRecentSearchesUseCase
import com.example.curate.domain.usecase.DeleteRecentSearchUseCase
import com.example.curate.domain.usecase.GetWallpaperFeedUseCase
import com.example.curate.domain.usecase.ObserveRecentSearchesUseCase
import com.example.curate.domain.usecase.RecordRecentSearchUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has blank query and observed recents`() = runTest(dispatcher) {
        val historyRepository = FakeSearchHistoryRepository(
            initialSearches = listOf(RecentSearch("neon city", 1L))
        )
        val viewModel = createViewModel(historyRepository = historyRepository)
        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.query)
        assertEquals(listOf(RecentSearch("neon city", 1L)), viewModel.uiState.value.recentSearches)
    }

    @Test
    fun `query changes update state immediately`() = runTest(dispatcher) {
        val viewModel = createViewModel()

        viewModel.onSearchQueryChange("forest")

        assertEquals("forest", viewModel.uiState.value.query)
    }

    @Test
    fun `blank query does not call wallpaper search`() = runTest(dispatcher) {
        val wallpaperRepository = FakeWallpaperRepository()
        val viewModel = createViewModel(wallpaperRepository = wallpaperRepository)
        val collectJob = backgroundScope.launch { viewModel.wallpapers.collect {} }

        viewModel.onSearchQueryChange("   ")
        advanceTimeBy(401)
        advanceUntilIdle()

        assertEquals(emptyList<String>(), wallpaperRepository.queries)
        collectJob.cancel()
    }

    @Test
    fun `debounced nonblank query searches without recording recent`() = runTest(dispatcher) {
        val wallpaperRepository = FakeWallpaperRepository()
        val historyRepository = FakeSearchHistoryRepository()
        val viewModel = createViewModel(
            wallpaperRepository = wallpaperRepository,
            historyRepository = historyRepository
        )
        val collectJob = backgroundScope.launch { viewModel.wallpapers.collect {} }

        viewModel.onSearchQueryChange(" mountains ")
        advanceTimeBy(399)

        assertEquals(emptyList<String>(), wallpaperRepository.queries)

        advanceTimeBy(1)
        advanceUntilIdle()

        assertEquals(listOf("mountains"), wallpaperRepository.queries)
        assertEquals(emptyList<String>(), historyRepository.recordedQueries)
        assertEquals("mountains", viewModel.uiState.value.activeSearchQuery)
        collectJob.cancel()
    }

    @Test
    fun `submitted search records trimmed recent query`() = runTest(dispatcher) {
        val historyRepository = FakeSearchHistoryRepository()
        val viewModel = createViewModel(historyRepository = historyRepository)

        viewModel.onSearchQueryChange(" mountains ")
        viewModel.onSearchSubmitted()
        advanceUntilIdle()

        assertEquals(listOf("mountains"), historyRepository.recordedQueries)
    }

    @Test
    fun `submitted blank query does not record recent query`() = runTest(dispatcher) {
        val historyRepository = FakeSearchHistoryRepository()
        val viewModel = createViewModel(historyRepository = historyRepository)

        viewModel.onSearchQueryChange("   ")
        viewModel.onSearchSubmitted()
        advanceUntilIdle()

        assertEquals(emptyList<String>(), historyRepository.recordedQueries)
    }

    @Test
    fun `duplicate trimmed query is ignored`() = runTest(dispatcher) {
        val wallpaperRepository = FakeWallpaperRepository()
        val viewModel = createViewModel(wallpaperRepository = wallpaperRepository)
        val collectJob = backgroundScope.launch { viewModel.wallpapers.collect {} }

        viewModel.onSearchQueryChange("city")
        advanceTimeBy(401)
        advanceUntilIdle()
        viewModel.onSearchQueryChange(" city ")
        advanceTimeBy(401)
        advanceUntilIdle()

        assertEquals(listOf("city"), wallpaperRepository.queries)
        collectJob.cancel()
    }

    @Test
    fun `recent click updates query`() = runTest(dispatcher) {
        val viewModel = createViewModel()

        viewModel.onRecentSearchClick("desert")

        assertEquals("desert", viewModel.uiState.value.query)
    }

    @Test
    fun `delete and clear recent searches call use cases`() = runTest(dispatcher) {
        val historyRepository = FakeSearchHistoryRepository()
        val viewModel = createViewModel(historyRepository = historyRepository)

        viewModel.onDeleteRecentSearch("city")
        viewModel.onClearRecentSearches()
        advanceUntilIdle()

        assertEquals(listOf("city"), historyRepository.deletedQueries)
        assertEquals(1, historyRepository.clearCount)
    }

    @Test
    fun `scrolling down hides top bar and enables grid item animations`() = runTest(dispatcher) {
        val viewModel = createViewModel()

        viewModel.onScrollDirectionChanged(SearchScrollDirection.Up)
        viewModel.onScrollDirectionChanged(SearchScrollDirection.Down)

        assertFalse(viewModel.uiState.value.isTopBarVisible)
        assertTrue(viewModel.uiState.value.shouldAnimateGridItems)
    }

    @Test
    fun `scrolling up shows top bar and disables grid item animations`() = runTest(dispatcher) {
        val viewModel = createViewModel()

        viewModel.onScrollDirectionChanged(SearchScrollDirection.Down)
        viewModel.onScrollDirectionChanged(SearchScrollDirection.Up)

        assertTrue(viewModel.uiState.value.isTopBarVisible)
        assertFalse(viewModel.uiState.value.shouldAnimateGridItems)
    }

    @Test
    fun `completed grid item animation records wallpaper id once`() = runTest(dispatcher) {
        val viewModel = createViewModel()

        viewModel.onGridItemAnimationCompleted("wallpaper-one")
        viewModel.onGridItemAnimationCompleted("wallpaper-one")

        assertEquals(setOf("wallpaper-one"), viewModel.uiState.value.animatedGridItemIds)
    }

    private fun createViewModel(
        wallpaperRepository: WallpaperRepository = FakeWallpaperRepository(),
        historyRepository: SearchHistoryRepository = FakeSearchHistoryRepository()
    ): SearchViewModel {
        return SearchViewModel(
            getWallpaperFeed = GetWallpaperFeedUseCase(wallpaperRepository),
            observeRecentSearches = ObserveRecentSearchesUseCase(historyRepository),
            recordRecentSearch = RecordRecentSearchUseCase(historyRepository),
            deleteRecentSearch = DeleteRecentSearchUseCase(historyRepository),
            clearRecentSearches = ClearRecentSearchesUseCase(historyRepository)
        )
    }

    private class FakeWallpaperRepository : WallpaperRepository {
        val queries = mutableListOf<String>()

        override fun getWallpaperFeed(query: String): Flow<PagingData<Wallpaper>> {
            queries += query
            return flowOf(PagingData.empty())
        }

        override suspend fun getWallpaper(id: String): Wallpaper {
            return Wallpaper(
                id = id,
                width = 100,
                height = 150,
                previewUrl = "",
                fullUrl = "",
                blurHash = null,
                photographerName = "",
                downloadLocation = ""
            )
        }

        override fun observeIsFavorite(wallpaperId: String): Flow<Boolean> {
            return flowOf(false)
        }

        override suspend fun toggleFavorite(wallpaper: Wallpaper) = Unit
    }

    private class FakeSearchHistoryRepository(
        initialSearches: List<RecentSearch> = emptyList()
    ) : SearchHistoryRepository {
        private val searches = MutableStateFlow(initialSearches)
        val recordedQueries = mutableListOf<String>()
        val deletedQueries = mutableListOf<String>()
        var clearCount = 0

        override fun observeRecentSearches(): Flow<List<RecentSearch>> = searches

        override suspend fun recordSearch(query: String) {
            recordedQueries += query
        }

        override suspend fun deleteSearch(query: String) {
            deletedQueries += query
        }

        override suspend fun clearSearches() {
            clearCount++
        }
    }
}
