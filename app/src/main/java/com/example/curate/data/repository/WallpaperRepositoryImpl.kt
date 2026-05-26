package com.example.curate.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.curate.data.paging.WallpaperPagingSource
import com.example.curate.data.remote.unsplash.UnsplashApi
import com.example.curate.data.remote.unsplash.mapper.toDomain
import com.example.curate.domain.model.Wallpaper
import com.example.curate.domain.repository.WallpaperRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class WallpaperRepositoryImpl @Inject constructor(
    private val api: UnsplashApi
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

    override suspend fun getWallpaper(id: String): Wallpaper {
        return api.getPhoto(id).toDomain()
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
