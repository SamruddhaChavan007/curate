package com.example.curate.presentation.discover

import com.example.curate.domain.model.WallpaperCategory
import com.example.curate.domain.repository.CollectionRepository
import com.example.curate.domain.usecase.GetCollectionsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
class DiscoverViewModelTest {
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
    fun `initial state enables grid item animations`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.shouldAnimateGridItems)
        assertTrue(viewModel.uiState.value.isTopBarVisible)
    }

    @Test
    fun `scrolling down hides top bar and enables grid item animations`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onScrollDirectionChanged(DiscoverScrollDirection.Up)
        viewModel.onScrollDirectionChanged(DiscoverScrollDirection.Down)

        assertTrue(viewModel.uiState.value.shouldAnimateGridItems)
        assertFalse(viewModel.uiState.value.isTopBarVisible)
    }

    @Test
    fun `scrolling up shows top bar and disables grid item animations`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onScrollDirectionChanged(DiscoverScrollDirection.Down)
        viewModel.onScrollDirectionChanged(DiscoverScrollDirection.Up)

        assertFalse(viewModel.uiState.value.shouldAnimateGridItems)
        assertTrue(viewModel.uiState.value.isTopBarVisible)
    }

    @Test
    fun `completed grid item animation records collection id`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onGridItemAnimationCompleted("collection-one")

        assertEquals(setOf("collection-one"), viewModel.uiState.value.animatedGridItemIds)
    }

    @Test
    fun `completing same grid item animation twice keeps animated id set stable`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onGridItemAnimationCompleted("collection-one")
        viewModel.onGridItemAnimationCompleted("collection-one")

        assertEquals(setOf("collection-one"), viewModel.uiState.value.animatedGridItemIds)
    }

    private fun createViewModel(
        repository: CollectionRepository = FakeCollectionRepository()
    ): DiscoverViewModel {
        return DiscoverViewModel(
            getCollections = GetCollectionsUseCase(repository)
        )
    }

    private class FakeCollectionRepository : CollectionRepository {
        override suspend fun fetchCategories(
            page: Int,
            perPage: Int
        ): Result<List<WallpaperCategory>> {
            return Result.success(emptyList())
        }
    }
}
