package com.example.curate.domain.usecase

import androidx.paging.PagingData
import com.example.curate.domain.model.Wallpaper
import com.example.curate.domain.repository.WallpaperRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetSplashWallpapersUseCaseTest {
    @Test
    fun `delegates default wallpaper query and splash count to repository`() = runTest {
        val repository = FakeWallpaperRepository()
        val useCase = GetSplashWallpapersUseCase(repository)

        useCase()

        assertEquals(WallpaperRepository.DEFAULT_QUERY, repository.lastQuery)
        assertEquals(WallpaperRepository.SPLASH_WALLPAPER_COUNT, repository.lastCount)
    }

    private class FakeWallpaperRepository : WallpaperRepository {
        var lastQuery: String? = null
            private set
        var lastCount: Int? = null
            private set

        override fun getWallpaperFeed(query: String): Flow<PagingData<Wallpaper>> {
            return flowOf(PagingData.empty())
        }

        override suspend fun getSplashWallpapers(query: String, count: Int): List<Wallpaper> {
            lastQuery = query
            lastCount = count
            return emptyList()
        }

        override suspend fun getWallpaper(id: String): Wallpaper {
            error("Not used")
        }

        override fun observeIsFavorite(wallpaperId: String): Flow<Boolean> = flowOf(false)

        override suspend fun toggleFavorite(wallpaper: Wallpaper) = Unit
    }
}
