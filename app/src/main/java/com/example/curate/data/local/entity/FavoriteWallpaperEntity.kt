package com.example.curate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_wallpapers")
data class FavoriteWallpaperEntity(
    @PrimaryKey
    val wallpaperId: String,
    val previewUrl: String,
    val fullUrl: String,
    val blurHash: String?,
    val photographerName: String,
    val downloadLocation: String,
    val width: Int,
    val height: Int,
    val createdAt: Long = System.currentTimeMillis()
)