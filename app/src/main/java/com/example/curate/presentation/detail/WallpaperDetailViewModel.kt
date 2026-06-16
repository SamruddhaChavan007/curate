package com.example.curate.presentation.detail

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.curate.domain.model.Wallpaper
import com.example.curate.domain.model.safeUserMessage
import com.example.curate.domain.usecase.GetWallpaperDetailUseCase
import com.example.curate.domain.usecase.ObserveWallpaperFavoriteUseCase
import com.example.curate.domain.usecase.ToggleWallpaperFavoriteUseCase
import com.example.curate.presentation.home.toUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = WallpaperDetailViewModel.Factory::class)
class WallpaperDetailViewModel @AssistedInject constructor(
    @Assisted private val wallpaperId: String,
    private val getWallpaperDetail: GetWallpaperDetailUseCase,
    private val contrastAnalyzer: WallpaperButtonContrastAnalyzer,
    private val observeWallpaperFavoriteUseCase: ObserveWallpaperFavoriteUseCase,
    private val toggleWallpaperFavoriteUseCase: ToggleWallpaperFavoriteUseCase
) : ViewModel() {
    private var pendingBackButtonTint: BackButtonTint? = null
    private var lastAnalysisRequest: AnalysisRequest? = null
    private var analysisJob: Job? = null
    private var favoriteObservationJob: Job? = null

    private var cachedWallpaper: Wallpaper? = null

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
            try {
                val domainWallpaper = getWallpaperDetail(wallpaperId)
                cachedWallpaper = domainWallpaper
                _uiState.value = WallpaperDetailUiState.Content(
                    wallpaper = domainWallpaper.toUiModel(),
                    backButtonTint = pendingBackButtonTint ?: BackButtonTint.Light,
                    isFavorite = false
                )

                observeFavoriteStatus(domainWallpaper.id)
            } catch (error: Exception) {
                _uiState.value =
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
        analysisJob?.cancel()
        analysisJob = viewModelScope.launch {
            val tint = contrastAnalyzer.analyze(
                bitmap = bitmap,
                imageBounds = imageBounds,
                buttonBounds = buttonBounds
            )
            if (request != lastAnalysisRequest) return@launch

            pendingBackButtonTint = tint

            val currentState = _uiState.value
            if (currentState is WallpaperDetailUiState.Content && currentState.wallpaper.id == wallpaperId) {
                _uiState.value = currentState.copy(backButtonTint = tint)
            }
        }
    }

    private fun observeFavoriteStatus(id: String) {
        favoriteObservationJob?.cancel()
        favoriteObservationJob = viewModelScope.launch {
            observeWallpaperFavoriteUseCase(id).collect { isFavorited ->
                _uiState.update { currentState ->
                    if (currentState is WallpaperDetailUiState.Content && currentState.wallpaper.id == id) {
                        currentState.copy(isFavorite = isFavorited)
                    } else {
                        currentState
                    }
                }
            }
        }
    }

    fun onFavoriteClick() {
        val wallpaperToggle = cachedWallpaper ?: return
        viewModelScope.launch {
            try {
                toggleWallpaperFavoriteUseCase(wallpaperToggle)
            } catch (e: Exception) {
                //N/A
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
