package com.example.curate.data.remote.unsplash.mapper

import com.example.curate.data.remote.unsplash.dto.TopicsDto
import com.example.curate.domain.model.TopicsCategory

fun TopicsDto.toDomain(): TopicsCategory {
    return TopicsCategory(
        id = this.id,
        title = this.title,
        countLabel = "${this.totalPhotos} wallpapers",
        imageUrl = this.coverPhoto?.urls?.regular ?: "",
        blurHash = this.coverPhoto?.blurHash
    )
}
