package com.example.curate.presentation.home

import com.example.curate.domain.model.Wallpaper
import org.junit.Assert.assertEquals
import org.junit.Test

class WallpaperUiModelTest {
    @Test
    fun `toUiModel preserves blur hash`() {
        val wallpaper = Wallpaper(
            id = "wallpaper",
            width = 1080,
            height = 1920,
            previewUrl = "https://example.com/preview.jpg",
            fullUrl = "https://example.com/full.jpg",
            blurHash = "LGF5]+Yk^6#M@-5c,1J5@[or[Q6.",
            photographerName = "Photographer",
            downloadLocation = "https://example.com/download"
        )

        val uiModel = wallpaper.toUiModel()

        assertEquals(wallpaper.blurHash, uiModel.blurHash)
    }
}
