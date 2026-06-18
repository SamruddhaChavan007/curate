package com.example.curate.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.curate.domain.repository.WallpaperRepository
import com.example.curate.domain.usecase.GetSplashWallpapersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getSplashWallpapers: GetSplashWallpapersUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        loadSplashWallpapers()
        startSplashTimer()
    }

    private fun loadSplashWallpapers() {
        viewModelScope.launch {
            try {
                val wallpapers = getSplashWallpapers(
                    query = WallpaperRepository.DEFAULT_QUERY,
                    count = WallpaperRepository.SPLASH_WALLPAPER_COUNT
                )
                val (leftColumnImages, rightColumnImages) = wallpapers.toSplashColumns()
                _uiState.update { currentState ->
                    currentState.copy(
                        leftColumnImages = leftColumnImages,
                        rightColumnImages = rightColumnImages
                    )
                }
            } catch (error: Exception) {
                Timber.w(error, "Unable to load splash wallpapers")
            }
        }
    }

    private fun startSplashTimer() {
        viewModelScope.launch {
            delay(_uiState.value.durationMillis)
            _uiState.update { currentState ->
                currentState.copy(isVisible = false)
            }
        }
    }
}
