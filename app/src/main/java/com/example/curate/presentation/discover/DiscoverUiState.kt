package com.example.curate.presentation.discover

data class DiscoverUiState(
    val isTopBarVisible: Boolean = true,
    val shouldAnimateGridItems: Boolean = true,
    val animatedGridItemIds: Set<String> = emptySet()
)

enum class DiscoverScrollDirection {
    Up,
    Down
}
