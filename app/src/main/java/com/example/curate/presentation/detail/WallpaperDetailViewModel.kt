package com.example.curate.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.curate.domain.usecase.GetWallpaperDetailUseCase
import com.example.curate.presentation.home.toUiModel
import com.example.curate.presentation.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WallpaperDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getWallpaperDetail: GetWallpaperDetailUseCase
) : ViewModel() {
    private val wallpaperId: String? = savedStateHandle[Routes.WALLPAPER_ID_ARG]

    private val _uiState = MutableStateFlow<WallpaperDetailUiState>(WallpaperDetailUiState.Loading)
    val uiState: StateFlow<WallpaperDetailUiState> = _uiState.asStateFlow()

    init {
        loadWallpaper()
    }

    private fun loadWallpaper() {
        val id = wallpaperId
        if (id.isNullOrBlank()) {
            _uiState.value = WallpaperDetailUiState.Error("Wallpaper id is missing.")
            return
        }

        viewModelScope.launch {
            _uiState.value = WallpaperDetailUiState.Loading
            _uiState.value = try {
                WallpaperDetailUiState.Content(getWallpaperDetail(id).toUiModel())
            } catch (error: Exception) {
                WallpaperDetailUiState.Error(error.message ?: "Unable to load wallpaper.")
            }
        }
    }
}
