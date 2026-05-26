package com.example.curate.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.curate.domain.usecase.GetWallpaperFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@HiltViewModel
class HomeViewModel @Inject constructor(
    getWallpaperFeed: GetWallpaperFeedUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val wallpapers: Flow<PagingData<WallpaperUiModel>> = getWallpaperFeed()
        .map { pagingData -> pagingData.map { wallpaper -> wallpaper.toUiModel() } }
        .cachedIn(viewModelScope)

    fun onScrollDirectionChanged(direction: HomeScrollDirection) {
        val shouldShowTopBar = direction == HomeScrollDirection.Up
        _uiState.update { currentState ->
            if (currentState.isTopBarVisible == shouldShowTopBar) {
                currentState
            } else {
                currentState.copy(isTopBarVisible = shouldShowTopBar)
            }
        }
    }
}
