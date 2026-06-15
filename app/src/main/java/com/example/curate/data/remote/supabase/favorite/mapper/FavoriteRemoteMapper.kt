package com.example.curate.data.remote.supabase.favorite.mapper

import com.example.curate.data.remote.supabase.favorite.dto.SupabaseFavoriteWallpaperDto
import com.example.curate.domain.model.Wallpaper

fun Wallpaper.toSupabaseFavoriteWallpaperDto(userId: String): SupabaseFavoriteWallpaperDto {
    return SupabaseFavoriteWallpaperDto(
        userId = userId,
        wallpaperId = this.id,
        previewUrl = this.previewUrl,
        fullUrl = this.fullUrl,
        blurHash = this.blurHash,
        photographerName = this.photographerName,
        downloadLocation = this.downloadLocation,
        width = this.width,
        height = this.height
    )
}

fun SupabaseFavoriteWallpaperDto.toWallpaper(): Wallpaper {
    return Wallpaper(
        id = this.wallpaperId,
        previewUrl = this.previewUrl,
        fullUrl = this.fullUrl,
        blurHash = this.blurHash,
        photographerName = this.photographerName,
        downloadLocation = this.downloadLocation,
        width = this.width,
        height = this.height,
    )
}
