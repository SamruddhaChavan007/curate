package com.example.curate.presentation.library

import com.example.curate.presentation.home.WallpaperUiModel

data class LibraryUiState(
    val favorites: List<WallpaperUiModel> = emptyList()
)
