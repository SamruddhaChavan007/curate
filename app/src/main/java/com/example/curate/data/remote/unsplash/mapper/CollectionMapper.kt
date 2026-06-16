package com.example.curate.data.remote.unsplash.mapper

import com.example.curate.data.remote.unsplash.dto.CollectionDto
import com.example.curate.domain.model.WallpaperCategory

fun CollectionDto.toDomain(): WallpaperCategory {
    return WallpaperCategory(
        id = this.id,
        title = this.title,
        countLabel = "${this.totalPhotos} wallpapers",
        imageUrl = this.coverPhoto?.urls?.regular ?: "",
        blurHash = this.coverPhoto?.blurHash
    )
}