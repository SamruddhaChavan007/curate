package com.example.curate.presentation.home

data class HomeUiState(
    val isTopBarVisible: Boolean = true,
    val shouldAnimateGridItems: Boolean = true,
    val animatedGridItemIds: Set<String> = emptySet()
)

enum class HomeScrollDirection {
    Up,
    Down
}
