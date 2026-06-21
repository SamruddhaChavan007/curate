package com.example.curate.domain.repository

import androidx.paging.PagingData
import com.example.curate.domain.model.Wallpaper
import kotlinx.coroutines.flow.Flow

interface WallpaperRepository {
    fun getWallpaperFeed(query: String = DEFAULT_QUERY): Flow<PagingData<Wallpaper>>

    suspend fun getSplashWallpapers(
        query: String = DEFAULT_QUERY,
        count: Int = SPLASH_WALLPAPER_COUNT
    ): List<Wallpaper>

    suspend fun getWallpaper(id: String): Wallpaper

    companion object {
        const val DEFAULT_QUERY = "wallpaper"
        const val SPLASH_WALLPAPER_COUNT = 8
    }

    fun observeIsFavorite(wallpaperId: String): Flow<Boolean>

    fun observeFavorites(): Flow<List<Wallpaper>>

    suspend fun toggleFavorite(wallpaper: Wallpaper)
}
