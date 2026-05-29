package com.example.curate.presentation.detail

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.example.curate.domain.model.Wallpaper
import com.example.curate.domain.repository.WallpaperRepository
import com.example.curate.domain.usecase.GetWallpaperDetailUseCase
import com.example.curate.presentation.navigation.Routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class WallpaperDetailViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial content uses light back button tint`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        advanceUntilIdle()

        val content = viewModel.uiState.value as WallpaperDetailUiState.Content
        assertEquals(BackButtonTint.Light, content.backButtonTint)
    }

    @Test
    fun `image ready event updates back button tint from analyzer`() = runTest(testDispatcher) {
        val analyzer = FakeContrastAnalyzer(tint = BackButtonTint.Dark)
        val viewModel = createViewModel(analyzer = analyzer)
        advanceUntilIdle()

        viewModel.onWallpaperImageReady(
            wallpaperId = WALLPAPER_ID,
            bitmap = testBitmap(),
            imageBounds = VALID_IMAGE_BOUNDS,
            buttonBounds = VALID_BUTTON_BOUNDS
        )
        advanceUntilIdle()

        val content = viewModel.uiState.value as WallpaperDetailUiState.Content
        assertEquals(BackButtonTint.Dark, content.backButtonTint)
    }

    @Test
    fun `duplicate image ready events are ignored`() = runTest(testDispatcher) {
        val analyzer = FakeContrastAnalyzer(tint = BackButtonTint.Dark)
        val viewModel = createViewModel(analyzer = analyzer)
        val bitmap = testBitmap()
        advanceUntilIdle()

        repeat(2) {
            viewModel.onWallpaperImageReady(
                wallpaperId = WALLPAPER_ID,
                bitmap = bitmap,
                imageBounds = VALID_IMAGE_BOUNDS,
                buttonBounds = VALID_BUTTON_BOUNDS
            )
        }
        advanceUntilIdle()

        assertEquals(1, analyzer.analysisCount)
    }

    private fun createViewModel(
        analyzer: WallpaperButtonContrastAnalyzer = FakeContrastAnalyzer(BackButtonTint.Light)
    ): WallpaperDetailViewModel {
        return WallpaperDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf(Routes.WALLPAPER_ID_ARG to WALLPAPER_ID)),
            getWallpaperDetail = GetWallpaperDetailUseCase(FakeWallpaperRepository()),
            contrastAnalyzer = analyzer
        )
    }

    private fun testBitmap(): Bitmap {
        return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.WHITE)
        }
    }

    private class FakeContrastAnalyzer(
        private val tint: BackButtonTint
    ) : WallpaperButtonContrastAnalyzer() {
        var analysisCount = 0
            private set

        override fun analyze(
            bitmap: Bitmap,
            imageBounds: ImageBounds,
            buttonBounds: ImageBounds
        ): BackButtonTint {
            analysisCount += 1
            return tint
        }
    }

    private class FakeWallpaperRepository : WallpaperRepository {
        override fun getWallpaperFeed(query: String): Flow<PagingData<Wallpaper>> {
            error("Not used")
        }

        override suspend fun getWallpaper(id: String): Wallpaper {
            return Wallpaper(
                id = id,
                width = 1080,
                height = 1920,
                previewUrl = "https://example.com/preview.jpg",
                fullUrl = "https://example.com/full.jpg",
                blurHash = null,
                photographerName = "Photographer",
                downloadLocation = "https://example.com/download"
            )
        }
    }
}

private const val WALLPAPER_ID = "wallpaper"
private val VALID_IMAGE_BOUNDS = ImageBounds(left = 0, top = 0, right = 100, bottom = 100)
private val VALID_BUTTON_BOUNDS = ImageBounds(left = 0, top = 0, right = 48, bottom = 48)
