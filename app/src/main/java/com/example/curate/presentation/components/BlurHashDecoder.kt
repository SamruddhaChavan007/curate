package com.example.curate.presentation.components

import android.graphics.Bitmap
import androidx.annotation.VisibleForTesting
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow

object BlurHashDecoder {
    fun decode(
        blurHash: String?,
        width: Int = DEFAULT_SIZE,
        height: Int = DEFAULT_SIZE,
        punch: Float = 1f
    ): Bitmap? {
        if (blurHash.isNullOrBlank() || blurHash.length < MIN_LENGTH || width <= 0 || height <= 0) {
            return null
        }

        return runCatching {
            val sizeFlag = decode83(blurHash[0].toString())
            val numY = sizeFlag / MAX_COMPONENTS + 1
            val numX = sizeFlag % MAX_COMPONENTS + 1
            val expectedLength = HEADER_LENGTH + AC_LENGTH * numX * numY

            if (blurHash.length != expectedLength) {
                return null
            }

            val quantizedMaximumValue = decode83(blurHash[1].toString())
            val maximumValue = (quantizedMaximumValue + 1) / MAX_AC_VALUE * punch
            val colors = Array(numX * numY) { FloatArray(COLOR_CHANNELS) }

            colors[0] = decodeDc(decode83(blurHash.substring(2, 6)))
            for (index in 1 until colors.size) {
                val start = 4 + index * 2
                colors[index] = decodeAc(
                    value = decode83(blurHash.substring(start, start + 2)),
                    maximumValue = maximumValue
                )
            }

            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    var red = 0f
                    var green = 0f
                    var blue = 0f

                    for (j in 0 until numY) {
                        for (i in 0 until numX) {
                            val basis = cos(PI * x * i / width).toFloat() *
                                cos(PI * y * j / height).toFloat()
                            val color = colors[i + j * numX]
                            red += color[0] * basis
                            green += color[1] * basis
                            blue += color[2] * basis
                        }
                    }

                    pixels[x + y * width] = ALPHA_MASK or
                        (linearToSrgb(red) shl RED_SHIFT) or
                        (linearToSrgb(green) shl GREEN_SHIFT) or
                        linearToSrgb(blue)
                }
            }

            Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
        }.getOrNull()
    }

    @VisibleForTesting
    internal fun decode83(value: String): Int {
        var result = 0
        value.forEach { character ->
            val digit = DIGIT_CHARACTERS.indexOf(character)
            require(digit >= 0) { "Invalid BlurHash character: $character" }
            result = result * BASE83 + digit
        }
        return result
    }

    private fun decodeDc(value: Int): FloatArray {
        return floatArrayOf(
            srgbToLinear(value shr RED_SHIFT),
            srgbToLinear((value shr GREEN_SHIFT) and COLOR_MASK),
            srgbToLinear(value and COLOR_MASK)
        )
    }

    private fun decodeAc(value: Int, maximumValue: Float): FloatArray {
        val quantizedRed = value / (AC_COMPONENTS * AC_COMPONENTS)
        val quantizedGreen = (value / AC_COMPONENTS) % AC_COMPONENTS
        val quantizedBlue = value % AC_COMPONENTS

        return floatArrayOf(
            signedPow((quantizedRed - AC_CENTER) / AC_CENTER, AC_EXPONENT) * maximumValue,
            signedPow((quantizedGreen - AC_CENTER) / AC_CENTER, AC_EXPONENT) * maximumValue,
            signedPow((quantizedBlue - AC_CENTER) / AC_CENTER, AC_EXPONENT) * maximumValue
        )
    }

    private fun srgbToLinear(value: Int): Float {
        val normalized = (value and COLOR_MASK) / COLOR_MAX
        return if (normalized <= SRGB_THRESHOLD) {
            normalized / SRGB_LOW_MULTIPLIER
        } else {
            ((normalized + SRGB_OFFSET) / SRGB_HIGH_MULTIPLIER).pow(SRGB_EXPONENT)
        }
    }

    private fun linearToSrgb(value: Float): Int {
        val normalized = value.coerceIn(0f, 1f)
        val srgb = if (normalized <= LINEAR_THRESHOLD) {
            normalized * SRGB_LOW_MULTIPLIER
        } else {
            SRGB_HIGH_MULTIPLIER * normalized.pow(1f / SRGB_EXPONENT) - SRGB_OFFSET
        }
        return (srgb * COLOR_MAX + 0.5f).toInt().coerceIn(0, COLOR_MASK)
    }

    private fun signedPow(value: Float, exponent: Float): Float {
        return when {
            value < 0f -> -abs(value).pow(exponent)
            else -> value.pow(exponent)
        }
    }

    private const val DEFAULT_SIZE = 32
    private const val MIN_LENGTH = 6
    private const val HEADER_LENGTH = 4
    private const val AC_LENGTH = 2
    private const val BASE83 = 83
    private const val MAX_COMPONENTS = 9
    private const val COLOR_CHANNELS = 3
    private const val MAX_AC_VALUE = 166f
    private const val AC_COMPONENTS = 19
    private const val AC_CENTER = 9f
    private const val AC_EXPONENT = 2f
    private const val RED_SHIFT = 16
    private const val GREEN_SHIFT = 8
    private const val COLOR_MASK = 255
    private const val COLOR_MAX = 255f
    private const val ALPHA_MASK = -0x1000000
    private const val SRGB_THRESHOLD = 0.04045f
    private const val LINEAR_THRESHOLD = 0.0031308f
    private const val SRGB_LOW_MULTIPLIER = 12.92f
    private const val SRGB_HIGH_MULTIPLIER = 1.055f
    private const val SRGB_OFFSET = 0.055f
    private const val SRGB_EXPONENT = 2.4f
    private const val DIGIT_CHARACTERS =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz#$%*+,-.:;=?@[]^_{|}~"
}
