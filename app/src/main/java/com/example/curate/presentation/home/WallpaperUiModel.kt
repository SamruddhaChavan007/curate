package com.example.curate.presentation.home

import com.example.curate.domain.model.Wallpaper

data class WallpaperUiModel(
    val id: String,
    val previewUrl: String,
    val fullUrl: String,
    val blurHash: String?,
    val photographerName: String,
    val downloadLocation: String,
    val aspectRatio: Float
)

fun Wallpaper.toUiModel(): WallpaperUiModel {
    val ratio = if (width > 0 && height > 0) {
        width.toFloat() / height.toFloat()
    } else {
        DEFAULT_PORTRAIT_RATIO
    }

    return WallpaperUiModel(
        id = id,
        previewUrl = previewUrl,
        fullUrl = fullUrl,
        blurHash = blurHash,
        photographerName = photographerName,
        downloadLocation = downloadLocation,
        aspectRatio = ratio.coerceIn(MIN_PORTRAIT_RATIO, MAX_PORTRAIT_RATIO)
    )
}

private const val DEFAULT_PORTRAIT_RATIO = 0.67f
private const val MIN_PORTRAIT_RATIO = 0.45f
private const val MAX_PORTRAIT_RATIO = 0.85f
