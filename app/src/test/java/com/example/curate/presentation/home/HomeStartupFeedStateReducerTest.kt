package com.example.curate.presentation.home

import androidx.paging.LoadState
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeStartupFeedStateReducerTest {
    @Test
    fun `refresh success marks startup ready`() {
        val state = HomeStartupFeedStateReducer.fromRefreshLoadState(
            currentState = StartupFeedState.Loading,
            loadState = LoadState.NotLoading(endOfPaginationReached = false)
        )

        assertEquals(StartupFeedState.Ready, state)
    }

    @Test
    fun `refresh error marks startup ready`() {
        val state = HomeStartupFeedStateReducer.fromRefreshLoadState(
            currentState = StartupFeedState.Loading,
            loadState = LoadState.Error(IllegalStateException("No network"))
        )

        assertEquals(StartupFeedState.Ready, state)
    }

    @Test
    fun `timeout marks unresolved startup timed out`() {
        val state = HomeStartupFeedStateReducer.fromTimeout(StartupFeedState.Loading)

        assertEquals(StartupFeedState.TimedOut, state)
    }

    @Test
    fun `refresh does not override timeout`() {
        val state = HomeStartupFeedStateReducer.fromRefreshLoadState(
            currentState = StartupFeedState.TimedOut,
            loadState = LoadState.NotLoading(endOfPaginationReached = false)
        )

        assertEquals(StartupFeedState.TimedOut, state)
    }
}
