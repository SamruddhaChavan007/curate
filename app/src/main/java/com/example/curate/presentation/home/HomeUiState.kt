package com.example.curate.presentation.home

data class HomeUiState(
    val isTopBarVisible: Boolean = true
)

enum class HomeScrollDirection {
    Up,
    Down
}
