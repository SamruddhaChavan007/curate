package com.example.curate.data.remote.unsplash.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnsplashSearchResponseDto(
    val results: List<UnsplashPhotoDto> = emptyList(),
    @SerialName("total_pages")
    val totalPages: Int = 0
)

@Serializable
data class UnsplashPhotoDto(
    val id: String,
    val width: Int = 0,
    val height: Int = 0,
    @SerialName("blur_hash")
    val blurHash: String? = null,
    val urls: UnsplashPhotoUrlsDto = UnsplashPhotoUrlsDto(),
    val user: UnsplashUserDto = UnsplashUserDto(),
    val links: UnsplashPhotoLinksDto = UnsplashPhotoLinksDto()
)

@Serializable
data class UnsplashPhotoUrlsDto(
    val regular: String = "",
    val full: String = ""
)

@Serializable
data class UnsplashUserDto(
    val name: String = ""
)

@Serializable
data class UnsplashPhotoLinksDto(
    @SerialName("download_location")
    val downloadLocation: String = ""
)
