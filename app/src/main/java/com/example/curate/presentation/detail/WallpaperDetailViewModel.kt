package com.example.curate.presentation.detail

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.curate.domain.model.safeUserMessage
import com.example.curate.domain.usecase.GetWallpaperDetailUseCase
import com.example.curate.presentation.home.toUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = WallpaperDetailViewModel.Factory::class)
class WallpaperDetailViewModel @AssistedInject constructor(
    @Assisted private val wallpaperId: String,
    private val getWallpaperDetail: GetWallpaperDetailUseCase,
    private val contrastAnalyzer: WallpaperButtonContrastAnalyzer
) : ViewModel() {
    private var pendingBackButtonTint: BackButtonTint? = null
    private var lastAnalysisRequest: AnalysisRequest? = null

    private val _uiState = MutableStateFlow<WallpaperDetailUiState>(WallpaperDetailUiState.Loading)
    val uiState: StateFlow<WallpaperDetailUiState> = _uiState.asStateFlow()

    init {
        loadWallpaper()
    }

    @AssistedFactory
    interface Factory {
        fun create(wallpaperId: String): WallpaperDetailViewModel
    }

    private fun loadWallpaper() {
        if (wallpaperId.isBlank()) {
            _uiState.value = WallpaperDetailUiState.Error("Wallpaper id is missing.")
            return
        }

        viewModelScope.launch {
            _uiState.value = WallpaperDetailUiState.Loading
            _uiState.value = try {
                WallpaperDetailUiState.Content(
                    wallpaper = getWallpaperDetail(wallpaperId).toUiModel(),
                    backButtonTint = pendingBackButtonTint ?: BackButtonTint.Light
                )
            } catch (error: Exception) {
                WallpaperDetailUiState.Error(error.safeUserMessage("Unable to load wallpaper."))
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
