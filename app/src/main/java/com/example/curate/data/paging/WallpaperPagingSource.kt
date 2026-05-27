package com.example.curate.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.curate.data.remote.unsplash.UnsplashApi
import com.example.curate.data.remote.unsplash.mapper.toDomain
import com.example.curate.domain.model.Wallpaper
import timber.log.Timber

class WallpaperPagingSource(
    private val api: UnsplashApi,
    private val query: String
) : PagingSource<Int, Wallpaper>() {
    private val deduplicator = WallpaperDeduplicator()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Wallpaper> {
        val page = params.key ?: FIRST_PAGE

        return try {
            val response = api.searchPhotos(
                query = query,
                page = page,
                perPage = params.loadSize.coerceAtMost(MAX_PAGE_SIZE)
            )
            val wallpapers = response.results.map { it.toDomain() }
            val uniqueWallpapers = deduplicator.filterUnique(wallpapers)
            val duplicateCount = wallpapers.size - uniqueWallpapers.size

            if (duplicateCount > 0) {
                Timber.d("Dropped %d duplicate wallpapers from page %d", duplicateCount, page)
            }

            LoadResult.Page(
                data = uniqueWallpapers,
                prevKey = if (page == FIRST_PAGE) null else page - 1,
                nextKey = if (page >= response.totalPages || wallpapers.isEmpty()) null else page + 1
            )
        } catch (error: Exception) {
            Timber.e(error, "Unable to load wallpapers")
            LoadResult.Error(error)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Wallpaper>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val FIRST_PAGE = 1
        const val MAX_PAGE_SIZE = 30
    }
}
