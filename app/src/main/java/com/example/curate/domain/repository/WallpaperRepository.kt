package com.example.curate.domain.repository

import androidx.paging.PagingData
import com.example.curate.domain.model.Wallpaper
import kotlinx.coroutines.flow.Flow

interface WallpaperRepository {
    fun getWallpaperFeed(query: String = DEFAULT_QUERY): Flow<PagingData<Wallpaper>>

    companion object {
        const val DEFAULT_QUERY = "wallpaper"
    }
}
