package com.example.curate.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.curate.data.local.dao.FavoriteWallpaperDao
import com.example.curate.data.local.entity.FavoriteWallpaperEntity

@Database(
    entities = [FavoriteWallpaperEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteWallpaperDao(): FavoriteWallpaperDao
}