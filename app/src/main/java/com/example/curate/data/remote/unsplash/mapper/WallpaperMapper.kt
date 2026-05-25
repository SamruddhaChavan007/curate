package com.example.curate.data.remote.unsplash.mapper

import com.example.curate.data.remote.unsplash.dto.UnsplashPhotoDto
import com.example.curate.domain.model.Wallpaper

fun UnsplashPhotoDto.toDomain(): Wallpaper {
    return Wallpaper(
        id = id,
        width = width,
        height = height,
        previewUrl = urls.regular,
        fullUrl = urls.full.ifBlank { urls.regular },
        blurHash = blurHash,
        photographerName = user.name.ifBlank { "Unknown photographer" },
        downloadLocation = links.downloadLocation
    )
}
