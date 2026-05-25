package com.example.curate.domain.model

data class Wallpaper(
    val id: String,
    val width: Int,
    val height: Int,
    val previewUrl: String,
    val fullUrl: String,
    val blurHash: String?,
    val photographerName: String,
    val downloadLocation: String
)
