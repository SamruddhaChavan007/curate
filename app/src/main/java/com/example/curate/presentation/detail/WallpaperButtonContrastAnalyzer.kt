package com.example.curate.presentation.detail

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt

open class WallpaperButtonContrastAnalyzer @Inject constructor() {
    open fun analyze(
        bitmap: Bitmap,
        imageBounds: ImageBounds,
        buttonBounds: ImageBounds
    ): BackButtonTint {
        if (bitmap.width <= 0 || bitmap.height <= 0 || !imageBounds.isValid() || !buttonBounds.isValid()) {
            return BackButtonTint.Light
        }

        val sampleBounds = buttonBounds.expand(BUTTON_SAMPLE_PADDING_PX)
        val relativeBounds = sampleBounds.intersect(imageBounds)?.translate(
            dx = -imageBounds.left,
            dy = -imageBounds.top
        ) ?: return BackButtonTint.Light

        val sourceBounds = relativeBounds.toSourceBounds(
            bitmapWidth = bitmap.width,
            bitmapHeight = bitmap.height,
            containerWidth = imageBounds.width,
            containerHeight = imageBounds.height
        ) ?: return BackButtonTint.Light

        val readableBitmap = bitmap.toReadableBitmap()
        val sample = Bitmap.createBitmap(
            readableBitmap,
            sourceBounds.left,
            sourceBounds.top,
            sourceBounds.width,
            sourceBounds.height
        )

        return try {
            val palette = Palette.from(sample)
                .clearFilters()
                .maximumColorCount(PALETTE_COLOR_COUNT)
                .generate()
            val backgroundColor = palette.weightedAverageColor() ?: sample.averageColor()
            backgroundColor.toBackButtonTint()
        } finally {
            if (sample != readableBitmap) {
                sample.recycle()
            }
            if (readableBitmap != bitmap) {
                readableBitmap.recycle()
            }
        }
    }

    private fun ImageBounds.expand(padding: Int): ImageBounds {
        return copy(
            left = left - padding,
            top = top - padding,
            right = right + padding,
            bottom = bottom + padding
        )
    }

    private fun ImageBounds.intersect(other: ImageBounds): ImageBounds? {
        val intersection = ImageBounds(
            left = max(left, other.left),
            top = max(top, other.top),
            right = minOf(right, other.right),
            bottom = minOf(bottom, other.bottom)
        )
        return intersection.takeIf { it.isValid() }
    }

    private fun ImageBounds.translate(dx: Int, dy: Int): ImageBounds {
        return copy(
            left = left + dx,
            top = top + dy,
            right = right + dx,
            bottom = bottom + dy
        )
    }

    private fun ImageBounds.toSourceBounds(
        bitmapWidth: Int,
        bitmapHeight: Int,
        containerWidth: Int,
        containerHeight: Int
    ): SourceBounds? {
        val scale = max(
            containerWidth.toFloat() / bitmapWidth.toFloat(),
            containerHeight.toFloat() / bitmapHeight.toFloat()
        )
        if (scale <= 0f) return null

        val scaledWidth = bitmapWidth * scale
        val scaledHeight = bitmapHeight * scale
        val offsetX = (containerWidth - scaledWidth) / 2f
        val offsetY = (containerHeight - scaledHeight) / 2f

        val sourceLeft = floor((left - offsetX) / scale).roundToInt().coerceIn(0, bitmapWidth - 1)
        val sourceTop = floor((top - offsetY) / scale).roundToInt().coerceIn(0, bitmapHeight - 1)
        val sourceRight = ceil((right - offsetX) / scale).roundToInt().coerceIn(sourceLeft + 1, bitmapWidth)
        val sourceBottom = ceil((bottom - offsetY) / scale).roundToInt().coerceIn(sourceTop + 1, bitmapHeight)

        return SourceBounds(
            left = sourceLeft,
            top = sourceTop,
            width = sourceRight - sourceLeft,
            height = sourceBottom - sourceTop
        ).takeIf { it.width > 0 && it.height > 0 }
    }

    private fun Bitmap.averageColor(): Int {
        var red = 0L
        var green = 0L
        var blue = 0L
        val pixelCount = width * height
        val pixels = IntArray(pixelCount)
        getPixels(pixels, 0, width, 0, 0, width, height)

        pixels.forEach { pixel ->
            red += Color.red(pixel)
            green += Color.green(pixel)
            blue += Color.blue(pixel)
        }

        return Color.rgb(
            (red / pixelCount).toInt(),
            (green / pixelCount).toInt(),
            (blue / pixelCount).toInt()
        )
    }

    private fun Bitmap.toReadableBitmap(): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && config == Bitmap.Config.HARDWARE) {
            copy(Bitmap.Config.ARGB_8888, false)
        } else {
            this
        }
    }

    private fun Palette.weightedAverageColor(): Int? {
        val totalPopulation = swatches.sumOf { it.population }
        if (totalPopulation <= 0) return null

        var red = 0L
        var green = 0L
        var blue = 0L
        swatches.forEach { swatch ->
            red += Color.red(swatch.rgb) * swatch.population.toLong()
            green += Color.green(swatch.rgb) * swatch.population.toLong()
            blue += Color.blue(swatch.rgb) * swatch.population.toLong()
        }

        return Color.rgb(
            (red / totalPopulation).toInt(),
            (green / totalPopulation).toInt(),
            (blue / totalPopulation).toInt()
        )
    }

    private fun Int.toBackButtonTint(): BackButtonTint {
        val contrastWithWhite = ColorUtils.calculateContrast(Color.WHITE, this)
        val contrastWithBlack = ColorUtils.calculateContrast(Color.BLACK, this)
        return if (contrastWithWhite >= contrastWithBlack) {
            BackButtonTint.Light
        } else {
            BackButtonTint.Dark
        }
    }

    private data class SourceBounds(
        val left: Int,
        val top: Int,
        val width: Int,
        val height: Int
    )
}

private const val BUTTON_SAMPLE_PADDING_PX = 12
private const val PALETTE_COLOR_COUNT = 8
