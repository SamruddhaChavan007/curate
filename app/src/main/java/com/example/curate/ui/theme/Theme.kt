package com.example.curate.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CurateDarkOnSurface,
    onPrimary = CurateDarkBg,
    primaryContainer = CurateDarkOnSurface,
    onPrimaryContainer = CurateDarkBg,
    secondary = CurateDarkOnSurface,
    onSecondary = CurateDarkBg,
    secondaryContainer = CurateDarkChrome,
    onSecondaryContainer = CurateDarkOnSurface,
    tertiary = CurateDarkOnSurface,
    onTertiary = CurateDarkBg,
    background = CurateDarkBg,
    onBackground = CurateDarkOnSurface,
    surface = CurateDarkBg,
    onSurface = CurateDarkOnSurface,
    surfaceVariant = CurateDarkChrome,
    onSurfaceVariant = CurateDarkOnSurface.copy(alpha = 0.62f),
    outline = CurateDarkOnSurface.copy(alpha = 0.10f),
    outlineVariant = CurateDarkOnSurface.copy(alpha = 0.10f),
    inverseSurface = CurateDarkOnSurface,
    inverseOnSurface = CurateDarkBg,
    inversePrimary = CurateDarkBg,
    error = CurateError,
    onError = CurateDarkBg,
    errorContainer = CurateError.copy(alpha = 0.18f),
    onErrorContainer = CurateDarkOnSurface,
    scrim = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = CurateLightOnSurface,
    onPrimary = CurateLightBg,
    primaryContainer = CurateLightOnSurface,
    onPrimaryContainer = CurateLightBg,
    secondary = CurateLightOnSurface,
    onSecondary = CurateLightBg,
    secondaryContainer = CurateLightChrome,
    onSecondaryContainer = CurateLightOnSurface,
    tertiary = CurateLightOnSurface,
    onTertiary = CurateLightBg,
    background = CurateLightBg,
    onBackground = CurateLightOnSurface,
    surface = CurateLightBg,
    onSurface = CurateLightOnSurface,
    surfaceVariant = CurateLightChrome,
    onSurfaceVariant = Color(0xB81A1916),
    outline = Color(0x1F1A1916),
    outlineVariant = Color(0x1F1A1916),
    inverseSurface = CurateLightOnSurface,
    inverseOnSurface = CurateLightBg,
    inversePrimary = CurateLightBg,
    error = CurateError,
    onError = CurateLightBg,
    errorContainer = CurateError.copy(alpha = 0.14f),
    onErrorContainer = CurateLightOnSurface,
    scrim = Color.Black
)

private val DarkCurateColors = CurateColors(
    bg = CurateDarkBg,
    chrome = CurateDarkChrome,
    onSurface = CurateDarkOnSurface,
    onSubtle = CurateDarkOnSurface.copy(alpha = 0.62f),
    onFaint = CurateDarkOnSurface.copy(alpha = 0.34f),
    hairline = CurateDarkOnSurface.copy(alpha = 0.10f),
    frameRing = CurateDarkFrameRing,
    avatarGradientStart = CurateAvatarGradientStart,
    avatarGradientEnd = CurateAvatarGradientEnd,
    overlayText = CurateOverlayText,
    darkScrim = Color.Black.copy(alpha = 0.55f),
    lightScrim = Color.Black.copy(alpha = 0.42f),
    success = CurateSuccess
)

private val LightCurateColors = CurateColors(
    bg = CurateLightBg,
    chrome = CurateLightChrome,
    onSurface = CurateLightOnSurface,
    onSubtle = Color(0x9E1A1916),
    onFaint = Color(0x571A1916),
    hairline = Color(0x1A1A1916),
    frameRing = CurateLightFrameRing,
    avatarGradientStart = CurateAvatarGradientStart,
    avatarGradientEnd = CurateAvatarGradientEnd,
    overlayText = CurateOverlayText,
    darkScrim = Color.Black.copy(alpha = 0.55f),
    lightScrim = Color.Black.copy(alpha = 0.42f),
    success = CurateSuccess
)

val LocalCurateColors = staticCompositionLocalOf { DarkCurateColors }

val MaterialTheme.curateColors: CurateColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCurateColors.current

@Composable
fun CurateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val curateColors = if (darkTheme) DarkCurateColors else LightCurateColors

    CompositionLocalProvider(LocalCurateColors provides curateColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
