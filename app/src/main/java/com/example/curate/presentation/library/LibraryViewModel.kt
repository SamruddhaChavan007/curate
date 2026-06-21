package com.example.curate.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.curate.domain.usecase.ObserveFavoriteWallpapersUseCase
import com.example.curate.presentation.home.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    observeFavoriteWallpapers: ObserveFavoriteWallpapersUseCase
): ViewModel() {
    val uiState = observeFavoriteWallpapers()
        .map { wallpapers ->
            LibraryUiState(
                favorites = wallpapers.map { wallpaper -> wallpaper.toUiModel() }
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            LibraryUiState()
        )
}
