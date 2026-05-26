package com.example.curate.presentation.detail

import com.example.curate.presentation.home.WallpaperUiModel

sealed interface WallpaperDetailUiState {
    data object Loading : WallpaperDetailUiState

    data class Content(
        val wallpaper: WallpaperUiModel
    ) : WallpaperDetailUiState

    data class Error(
        val message: String
    ) : WallpaperDetailUiState
}
