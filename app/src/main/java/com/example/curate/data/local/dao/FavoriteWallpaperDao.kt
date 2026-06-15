package com.example.curate.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.curate.data.local.entity.FavoriteWallpaperEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteWallpaperDao {
    @Upsert
    suspend fun insertFavorite(wallpaper: FavoriteWallpaperEntity)

    @Query("DELETE FROM favorite_wallpapers WHERE wallpaperId = :wallpaperId")
    suspend fun deleteFavoriteById(wallpaperId: String)

    @Query("SELECT * FROM favorite_wallpapers ORDER BY createdAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteWallpaperEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_wallpapers WHERE wallpaperId = :wallpaperId)")
    fun isFavorite(wallpaperId: String): Flow<Boolean>
}