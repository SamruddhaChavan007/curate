package com.example.curate.data.remote.supabase.favorite


import com.example.curate.data.remote.supabase.favorite.dto.SupabaseFavoriteWallpaperDto
import com.example.curate.data.remote.supabase.favorite.mapper.toSupabaseFavoriteWallpaperDto
import com.example.curate.data.remote.supabase.favorite.mapper.toWallpaper
import com.example.curate.domain.model.Wallpaper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class SupabaseFavoriteGatewayImpl @Inject constructor(
    private val supabaseClientProvider: Provider<SupabaseClient>
) : SupabaseFavoriteGateway {
    private val postgrest get() = supabaseClientProvider.get().postgrest

    override suspend fun upsertFavorite(userId: String, wallpaper: Wallpaper) {
        val dto = wallpaper.toSupabaseFavoriteWallpaperDto(userId)

        postgrest.from("favorite_wallpapers").upsert(dto)
    }

    override suspend fun deleteFavorite(userId: String, wallpaperId: String) {
        postgrest.from("favorite_wallpapers").delete {
            filter {
                eq("user_id", userId)
                eq("wallpaper_id", wallpaperId)
            }
        }
    }

    override suspend fun getFavorites(userId: String): List<Wallpaper> {
        val dtos = postgrest.from("favorite_wallpapers")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<SupabaseFavoriteWallpaperDto>()

        return dtos.map { dto ->
            dto.toWallpaper()
        }
    }
}