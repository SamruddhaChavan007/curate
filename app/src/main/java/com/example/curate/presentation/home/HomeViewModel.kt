package com.example.curate.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.curate.domain.usecase.GetWallpaperFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getWallpaperFeed: GetWallpaperFeedUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val wallpapers: Flow<PagingData<WallpaperUiModel>> = getWallpaperFeed()
        .map { pagingData -> pagingData.map { wallpaper -> wallpaper.toUiModel() } }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            delay(SPLASH_TIMEOUT_MILLIS)
            _uiState.update { currentState ->
                val nextStartupState = HomeStartupFeedStateReducer.fromTimeout(
                    currentState = currentState.startupFeedState
                )
                if (nextStartupState == currentState.startupFeedState) {
                    currentState
                } else {
                    currentState.copy(startupFeedState = nextStartupState)
                }
            }
        }
    }

    fun onInitialRefreshStateChanged(loadState: LoadState) {
        _uiState.update { currentState ->
            val nextStartupState = HomeStartupFeedStateReducer.fromRefreshLoadState(
                currentState = currentState.startupFeedState,
                loadState = loadState
            )
            if (currentState.startupFeedState == nextStartupState) {
                currentState
            } else {
                currentState.copy(startupFeedState = nextStartupState)
            }
        }
    }

    fun onScrollDirectionChanged(direction: HomeScrollDirection) {
        val shouldShowTopBar = direction == HomeScrollDirection.Up
        val shouldAnimateGridItems = direction == HomeScrollDirection.Down
        _uiState.update { currentState ->
            if (
                currentState.isTopBarVisible == shouldShowTopBar &&
                currentState.shouldAnimateGridItems == shouldAnimateGridItems
            ) {
                currentState
            } else {
                currentState.copy(
                    isTopBarVisible = shouldShowTopBar,
                    shouldAnimateGridItems = shouldAnimateGridItems
                )
            }
        }
    }

    fun onGridItemAnimationCompleted(itemId: String) {
        _uiState.update { currentState ->
            if (itemId in currentState.animatedGridItemIds) {
                currentState
            } else {
                currentState.copy(
                    animatedGridItemIds = currentState.animatedGridItemIds + itemId
                )
            }
        }
    }

    private companion object {
        const val SPLASH_TIMEOUT_MILLIS = 1_200L
    }
}
