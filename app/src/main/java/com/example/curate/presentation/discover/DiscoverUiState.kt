package com.example.curate.presentation.discover

import com.example.curate.domain.model.WallpaperCategory

data class DiscoverUiState(
    val isLoading: Boolean = true,
    val collections: List<WallpaperCategory> = emptyList(),
    val errorMessage: String? = null,
    val isTopBarVisible: Boolean = true,
    val shouldAnimateGridItems: Boolean = true,
    val animatedGridItemIds: Set<String> = emptySet()
)

enum class DiscoverScrollDirection {
    Up,
    Down
}
