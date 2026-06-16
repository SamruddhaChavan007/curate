package com.example.curate.presentation.discover

import androidx.paging.PagingData
import com.example.curate.domain.model.TopicsCategory
import com.example.curate.domain.repository.TopicsRepository
import com.example.curate.domain.usecase.GetTopicsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
    fun `completed grid item animation records topic id`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onGridItemAnimationCompleted("topic-one")

        assertEquals(setOf("topic-one"), viewModel.uiState.value.animatedGridItemIds)
    }

    @Test
    fun `completing same grid item animation twice keeps animated id set stable`() = runTest(dispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onGridItemAnimationCompleted("topic-one")
        viewModel.onGridItemAnimationCompleted("topic-one")

        assertEquals(setOf("topic-one"), viewModel.uiState.value.animatedGridItemIds)
    }

    private fun createViewModel(
        repository: TopicsRepository = FakeTopicsRepository()
    ): DiscoverViewModel {
        return DiscoverViewModel(
            getTopics = GetTopicsUseCase(repository)
        )
    }

    private class FakeTopicsRepository : TopicsRepository {
        override fun getTopicFeed(): Flow<PagingData<TopicsCategory>> {
            return flowOf(PagingData.empty())
        }
    }
}
