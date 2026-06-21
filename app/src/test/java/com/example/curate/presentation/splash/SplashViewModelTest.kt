package com.example.curate.presentation.splash

import androidx.paging.PagingData
import com.example.curate.domain.model.Wallpaper
import com.example.curate.domain.repository.WallpaperRepository
import com.example.curate.domain.usecase.GetSplashWallpapersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {
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
    fun `initial state shows bundled fallback content immediately`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertTrue(state.isVisible)
        assertEquals(3_000L, state.durationMillis)
        assertEquals("Curate", state.wordmark)
        assertEquals("discover \u00b7 collect \u00b7 share", state.tagline)
        assertEquals(4, state.leftColumnImages.size)
        assertEquals(4, state.rightColumnImages.size)
        assertTrue(state.leftColumnImages.all { image -> image.remoteUrl == null })
        assertTrue(state.rightColumnImages.all { image -> image.remoteUrl == null })
    }

    @Test
    fun `successful splash fetch replaces fallback primary urls`() = runTest(dispatcher) {
        val repository = FakeWallpaperRepository(
            splashWallpapers = (0 until 8).map { index -> wallpaper(index) }
        )
        val viewModel = createViewModel(repository)

        runCurrent()
        val state = viewModel.uiState.value

        assertEquals("wallpaper", repository.lastQuery)
        assertEquals(8, repository.lastCount)
        assertEquals("https://example.com/preview-0.jpg", state.leftColumnImages.first().remoteUrl)
        assertEquals("https://example.com/preview-4.jpg", state.rightColumnImages.first().remoteUrl)
    }

    @Test
    fun `failed splash fetch keeps bundled fallback content`() = runTest(dispatcher) {
        val viewModel = createViewModel(
            FakeWallpaperRepository(error = IllegalStateException("Offline"))
        )

        runCurrent()
        val state = viewModel.uiState.value

        assertNull(state.leftColumnImages.first().remoteUrl)
        assertNull(state.rightColumnImages.first().remoteUrl)
    }

    @Test
    fun `splash remains visible before configured duration`() = runTest(dispatcher) {
        val viewModel = createViewModel()

        advanceTimeBy(2_999L)
        runCurrent()

        assertTrue(viewModel.uiState.value.isVisible)
    }

    @Test
    fun `splash completes after configured duration regardless of fetch state`() = runTest(dispatcher) {
        val viewModel = createViewModel(
            FakeWallpaperRepository(error = IllegalStateException("Offline"))
        )

        advanceTimeBy(3_000L)
        runCurrent()

        assertFalse(viewModel.uiState.value.isVisible)
    }

    private fun createViewModel(
        repository: FakeWallpaperRepository = FakeWallpaperRepository()
    ): SplashViewModel {
        return SplashViewModel(
            getSplashWallpapers = GetSplashWallpapersUseCase(repository)
        )
    }

    private class FakeWallpaperRepository(
        private val splashWallpapers: List<Wallpaper> = emptyList(),
        private val error: Exception? = null
    ) : WallpaperRepository {
        var lastQuery: String? = null
            private set
        var lastCount: Int? = null
            private set

        override fun getWallpaperFeed(query: String): Flow<PagingData<Wallpaper>> {
            return flowOf(PagingData.empty())
        }

        override suspend fun getSplashWallpapers(query: String, count: Int): List<Wallpaper> {
            lastQuery = query
            lastCount = count
            error?.let { throw it }
            return splashWallpapers
        }

        override suspend fun getWallpaper(id: String): Wallpaper {
            error("Not used")
        }

        override fun observeIsFavorite(wallpaperId: String): Flow<Boolean> = flowOf(false)

        override fun observeFavorites(): Flow<List<Wallpaper>> = flowOf(emptyList())

        override suspend fun toggleFavorite(wallpaper: Wallpaper) = Unit
    }

    private fun wallpaper(index: Int): Wallpaper {
        return Wallpaper(
            id = "wallpaper-$index",
            width = 1000,
            height = 1500,
            previewUrl = "https://example.com/preview-$index.jpg",
            fullUrl = "https://example.com/full-$index.jpg",
            blurHash = "blur-$index",
            photographerName = "Photographer $index",
            downloadLocation = "https://example.com/download-$index"
        )
    }
}
