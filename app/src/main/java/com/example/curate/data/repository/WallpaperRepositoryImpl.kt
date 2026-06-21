package com.example.curate.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.curate.data.error.AppErrorMapper
import com.example.curate.data.local.dao.FavoriteWallpaperDao
import com.example.curate.data.local.mapper.toDomain
import com.example.curate.data.local.mapper.toEntity
import com.example.curate.data.paging.WallpaperPagingSource
import com.example.curate.data.remote.supabase.favorite.SupabaseFavoriteGateway
import com.example.curate.data.remote.unsplash.UnsplashApi
import com.example.curate.data.remote.unsplash.mapper.toDomain
import com.example.curate.data.remote.unsplash.mapper.toSplashDomain
import com.example.curate.domain.model.AuthState
import com.example.curate.domain.model.Wallpaper
import com.example.curate.domain.repository.AuthRepository
import com.example.curate.domain.repository.WallpaperRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class WallpaperRepositoryImpl @Inject constructor(
    private val api: UnsplashApi,
    private val favoriteWallpaperDao: FavoriteWallpaperDao,
    private val supabaseFavoriteGateway: SupabaseFavoriteGateway,
    private val authRepository: AuthRepository
) : WallpaperRepository {
    override fun getWallpaperFeed(query: String): Flow<PagingData<Wallpaper>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                WallpaperPagingSource(
                    api = api,
                    query = query
                )
            }
        ).flow
    }

    override suspend fun getSplashWallpapers(query: String, count: Int): List<Wallpaper> {
        return try {
            api.searchPhotos(
                query = query,
                page = SPLASH_PAGE,
                perPage = count
            ).results.map { photo -> photo.toSplashDomain() }
        } catch (error: Exception) {
            throw AppErrorMapper.toException(error)
        }
    }

    override suspend fun getWallpaper(id: String): Wallpaper {
        return try {
            api.getPhoto(id).toDomain()
        } catch (error: Exception) {
            throw AppErrorMapper.toException(error)
        }
    }

    private companion object {
        const val PAGE_SIZE = 20
        const val SPLASH_PAGE = 1
    }

    override fun observeIsFavorite(wallpaperId: String): Flow<Boolean> {
        return favoriteWallpaperDao.isFavorite(wallpaperId)
    }

    override suspend fun toggleFavorite(wallpaper: Wallpaper) {
        val userId = currentUserId()
        if (userId == null) {
            Timber.w("Ignoring favorite toggle because user is unauthenticated")
            return
        }

        val isCurrentlyFavorited = favoriteWallpaperDao.isFavorite(wallpaper.id).first()

        if (isCurrentlyFavorited) {
            favoriteWallpaperDao.deleteFavoriteById(wallpaper.id)
            syncFavoriteChange(userId) { authenticatedUserId ->
                supabaseFavoriteGateway.deleteFavorite(
                    userId = authenticatedUserId,
                    wallpaperId = wallpaper.id
                )
            }
        } else {
            favoriteWallpaperDao.insertFavorite(wallpaper.toEntity())
            syncFavoriteChange(userId) { authenticatedUserId ->
                supabaseFavoriteGateway.upsertFavorite(
                    userId = authenticatedUserId,
                    wallpaper = wallpaper
                )
            }
        }
    }

    private fun currentUserId(): String? {
        return (authRepository.authState.value as? AuthState.Authenticated)?.user?.id
    }

    private suspend fun syncFavoriteChange(
        userId: String?,
        sync: suspend (String) -> Unit
    ) {
        if (userId == null) return

        try {
            sync(userId)
        } catch (error: Exception) {
            Timber.e(error, "Unable to sync favorite wallpaper")
        }
    }

    override fun observeFavorites(): Flow<List<Wallpaper>> {
        return favoriteWallpaperDao.getAllFavorites()
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }
}
