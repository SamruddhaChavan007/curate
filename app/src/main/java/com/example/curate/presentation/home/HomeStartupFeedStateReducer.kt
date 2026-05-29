package com.example.curate.presentation.home

import androidx.paging.LoadState

internal object HomeStartupFeedStateReducer {
    fun fromRefreshLoadState(
        currentState: StartupFeedState,
        loadState: LoadState
    ): StartupFeedState {
        if (currentState == StartupFeedState.TimedOut) {
            return currentState
        }

        return when (loadState) {
            is LoadState.Loading -> StartupFeedState.Loading
            is LoadState.Error,
            is LoadState.NotLoading -> StartupFeedState.Ready
        }
    }

    fun fromTimeout(currentState: StartupFeedState): StartupFeedState {
        return if (currentState == StartupFeedState.Loading) {
            StartupFeedState.TimedOut
        } else {
            currentState
        }
    }
}
