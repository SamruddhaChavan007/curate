package com.example.curate.presentation.detail

import android.graphics.Bitmap
import android.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WallpaperButtonContrastAnalyzerTest {
    private val analyzer = WallpaperButtonContrastAnalyzer()
    private val imageBounds = ImageBounds(left = 0, top = 0, right = 100, bottom = 100)
    private val buttonBounds = ImageBounds(left = 0, top = 0, right = 48, bottom = 48)

    @Test
    fun `dark button region returns light tint`() {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.BLACK)
        }

        val tint = analyzer.analyze(bitmap, imageBounds, buttonBounds)

        assertEquals(BackButtonTint.Light, tint)
    }

    @Test
    fun `light button region returns dark tint`() {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.WHITE)
        }

        val tint = analyzer.analyze(bitmap, imageBounds, buttonBounds)

        assertEquals(BackButtonTint.Dark, tint)
    }

    @Test
    fun `mixed button region chooses strongest contrast`() {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.WHITE)
        repeat(bitmap.height) { y ->
            repeat(20) { x ->
                bitmap.setPixel(x, y, Color.BLACK)
            }
        }

        val centeredButtonBounds = ImageBounds(left = 25, top = 25, right = 75, bottom = 75)

        val tint = analyzer.analyze(bitmap, imageBounds, centeredButtonBounds)

        assertEquals(BackButtonTint.Dark, tint)
    }

    @Test
    fun `invalid sample falls back to light tint`() {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.WHITE)
        }
        val invalidButtonBounds = ImageBounds(left = 20, top = 20, right = 20, bottom = 20)

        val tint = analyzer.analyze(bitmap, imageBounds, invalidButtonBounds)

        assertEquals(BackButtonTint.Light, tint)
    }
}
