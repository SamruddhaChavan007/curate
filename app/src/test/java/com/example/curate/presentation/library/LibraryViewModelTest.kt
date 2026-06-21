package com.example.curate.presentation.library

import androidx.paging.PagingData
import com.example.curate.domain.model.Wallpaper
import com.example.curate.domain.repository.WallpaperRepository
import com.example.curate.domain.usecase.ObserveFavoriteWallpapersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `observed favorites are mapped into library ui state`() = runTest(dispatcher) {
        val repository = FakeWallpaperRepository()
        val viewModel = LibraryViewModel(ObserveFavoriteWallpapersUseCase(repository))
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }

        repository.favorites.value = listOf(wallpaper("favorite-one"))
        advanceUntilIdle()

        assertEquals(listOf("favorite-one"), viewModel.uiState.value.favorites.map { it.id })
        assertEquals(0.5f, viewModel.uiState.value.favorites.single().aspectRatio)

        collectJob.cancel()
    }

    private class FakeWallpaperRepository : WallpaperRepository {
        val favorites = MutableStateFlow<List<Wallpaper>>(emptyList())

        override fun getWallpaperFeed(query: String): Flow<PagingData<Wallpaper>> =
            flowOf(PagingData.empty())

        override suspend fun getSplashWallpapers(query: String, count: Int): List<Wallpaper> =
            emptyList()

        override suspend fun getWallpaper(id: String): Wallpaper = wallpaper(id)

        override fun observeIsFavorite(wallpaperId: String): Flow<Boolean> = flowOf(false)

        override fun observeFavorites(): Flow<List<Wallpaper>> = favorites

        override suspend fun toggleFavorite(wallpaper: Wallpaper) = Unit
    }

    private companion object {
        fun wallpaper(id: String) = Wallpaper(
            id = id,
            width = 100,
            height = 200,
            previewUrl = "preview",
            fullUrl = "full",
            blurHash = null,
            photographerName = "Photographer",
            downloadLocation = "download"
        )
    }
}
