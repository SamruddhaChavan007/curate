package com.example.curate.presentation.home

data class HomeUiState(
    val isTopBarVisible: Boolean = true,
    val shouldAnimateGridItems: Boolean = true,
    val animatedGridItemIds: Set<String> = emptySet(),
    val startupFeedState: StartupFeedState = StartupFeedState.Loading
)

enum class HomeScrollDirection {
    Up,
    Down
}

enum class StartupFeedState {
    Loading,
    Ready,
    TimedOut
}
