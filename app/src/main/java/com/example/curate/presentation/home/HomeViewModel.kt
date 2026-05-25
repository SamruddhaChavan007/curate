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

@HiltViewModel
class HomeViewModel @Inject constructor(
    getWallpaperFeed: GetWallpaperFeedUseCase
) : ViewModel() {
    val wallpapers: Flow<PagingData<WallpaperUiModel>> = getWallpaperFeed()
        .map { pagingData -> pagingData.map { wallpaper -> wallpaper.toUiModel() } }
        .cachedIn(viewModelScope)

    private val _selectedWallpaper = MutableStateFlow<WallpaperUiModel?>(null)
    val selectedWallpaper: StateFlow<WallpaperUiModel?> = _selectedWallpaper.asStateFlow()

    fun selectWallpaper(wallpaper: WallpaperUiModel) {
        _selectedWallpaper.value = wallpaper
    }
}
