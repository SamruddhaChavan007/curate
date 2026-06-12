package com.example.curate.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val CurateDarkBg = Color(0xFF0B0B0D)
val CurateDarkChrome = Color(0xFF16161A)
val CurateDarkOnSurface = Color(0xFFF2F0EA)
val CurateDarkFrameRing = Color(0xFF1F1F22)

val CurateLightBg = Color(0xFFF4F2EC)
val CurateLightChrome = Color(0xFFE7E4DA)
val CurateLightOnSurface = Color(0xFF161512)
val CurateLightFrameRing = Color(0xFFD6D2C5)

val CurateOverlayText = CurateDarkOnSurface
val CurateAvatarGradientStart = Color(0xFF9D7F68)
val CurateAvatarGradientEnd = Color(0xFF614540)

val CurateError = Color(0xFFD86B5F)
val CurateSuccess = Color(0xFF7B8F6A)

@Immutable
data class CurateColors(
    val bg: Color,
    val chrome: Color,
    val onSurface: Color,
    val onSubtle: Color,
    val onFaint: Color,
    val hairline: Color,
    val frameRing: Color,
    val avatarGradientStart: Color,
    val avatarGradientEnd: Color,
    val overlayText: Color,
    val darkScrim: Color,
    val lightScrim: Color,
    val success: Color
)