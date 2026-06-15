package com.example.curate.presentation.detail

import com.example.curate.presentation.home.WallpaperUiModel

sealed interface WallpaperDetailUiState {
    data object Loading : WallpaperDetailUiState

    data class Content(
        val wallpaper: WallpaperUiModel,
        val backButtonTint: BackButtonTint = BackButtonTint.Light,
        val isFavorite: Boolean = false,
    ) : WallpaperDetailUiState

    data class Error(
        val message: String
    ) : WallpaperDetailUiState
}

enum class BackButtonTint {
    Light,
    Dark
}