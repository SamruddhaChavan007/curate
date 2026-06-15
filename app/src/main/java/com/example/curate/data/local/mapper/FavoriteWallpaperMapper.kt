package com.example.curate.data.local.mapper

import com.example.curate.data.local.entity.FavoriteWallpaperEntity
import com.example.curate.domain.model.Wallpaper

fun Wallpaper.toEntity(): FavoriteWallpaperEntity{
    return FavoriteWallpaperEntity(
        wallpaperId = this.id,
        previewUrl = this.previewUrl,
        fullUrl = this.fullUrl,
        blurHash = this.blurHash,
        photographerName = this.photographerName,
        downloadLocation = this.downloadLocation,
        width = this.width,
        height = this.height,
        createdAt = System.currentTimeMillis()
    )
}

fun FavoriteWallpaperEntity.toDomain(): Wallpaper{
    return Wallpaper(
        id = this.wallpaperId,
        previewUrl = this.previewUrl,
        fullUrl = this.fullUrl,
        blurHash = this.blurHash,
        photographerName = this.photographerName,
        downloadLocation = this.downloadLocation,
        width = this.width,
        height = this.height
    )
}