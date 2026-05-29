package com.example.curate.presentation.detail

import android.graphics.Bitmap
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
    private val getWallpaperDetail: GetWallpaperDetailUseCase,
    private val contrastAnalyzer: WallpaperButtonContrastAnalyzer
) : ViewModel() {
    private val wallpaperId: String? = savedStateHandle[Routes.WALLPAPER_ID_ARG]
    private var pendingBackButtonTint: BackButtonTint? = null
    private var lastAnalysisRequest: AnalysisRequest? = null

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
                WallpaperDetailUiState.Content(
                    wallpaper = getWallpaperDetail(id).toUiModel(),
                    backButtonTint = pendingBackButtonTint ?: BackButtonTint.Light
                )
            } catch (error: Exception) {
                WallpaperDetailUiState.Error(error.message ?: "Unable to load wallpaper.")
            }
        }
    }

    fun onWallpaperImageReady(
        wallpaperId: String,
        bitmap: Bitmap,
        imageBounds: ImageBounds,
        buttonBounds: ImageBounds
    ) {
        if (wallpaperId != this.wallpaperId) return

        val request = AnalysisRequest(
            wallpaperId = wallpaperId,
            bitmapWidth = bitmap.width,
            bitmapHeight = bitmap.height,
            imageBounds = imageBounds,
            buttonBounds = buttonBounds
        )
        if (request == lastAnalysisRequest) return

        lastAnalysisRequest = request
        viewModelScope.launch {
            val tint = contrastAnalyzer.analyze(
                bitmap = bitmap,
                imageBounds = imageBounds,
                buttonBounds = buttonBounds
            )
            pendingBackButtonTint = tint

            val currentState = _uiState.value
            if (currentState is WallpaperDetailUiState.Content && currentState.wallpaper.id == wallpaperId) {
                _uiState.value = currentState.copy(backButtonTint = tint)
            }
        }
    }

    private data class AnalysisRequest(
        val wallpaperId: String,
        val bitmapWidth: Int,
        val bitmapHeight: Int,
        val imageBounds: ImageBounds,
        val buttonBounds: ImageBounds
    )
}
