package com.example.curate.data.remote.supabase.favorite.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseFavoriteWallpaperDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("wallpaper_id") val wallpaperId: String,
    @SerialName("preview_url") val previewUrl: String,
    @SerialName("full_url") val fullUrl: String,
    @SerialName("blur_hash") val blurHash: String?,
    @SerialName("photographer_name") val photographerName: String,
    @SerialName("download_location") val downloadLocation: String,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int,
    @SerialName("created_at") val createdAt: String? = null
)