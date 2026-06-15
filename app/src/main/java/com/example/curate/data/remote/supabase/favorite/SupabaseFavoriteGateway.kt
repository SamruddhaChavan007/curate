package com.example.curate.data.remote.supabase.favorite

import com.example.curate.domain.model.Wallpaper

interface SupabaseFavoriteGateway {
    suspend fun upsertFavorite(userId: String, wallpaper: Wallpaper)
    suspend fun deleteFavorite(userId: String, wallpaperId: String)
    suspend fun getFavorites(userId: String): List<Wallpaper>
}