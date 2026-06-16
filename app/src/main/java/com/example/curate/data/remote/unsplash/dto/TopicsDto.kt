package com.example.curate.data.remote.unsplash.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopicsDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val descriptions: String?,
    @SerialName("total_photos") val totalPhotos: Int,
    @SerialName("cover_photo") val coverPhoto: CoverPhotoDto?
)

@Serializable
data class CoverPhotoDto(
    @SerialName("id") val id: String,
    @SerialName("blur_hash") val blurHash: String?,
    @SerialName("urls") val urls: ImageUrlDto
)

@Serializable
data class ImageUrlDto(
    @SerialName("regular") val regular: String,
    @SerialName("small") val small: String
)