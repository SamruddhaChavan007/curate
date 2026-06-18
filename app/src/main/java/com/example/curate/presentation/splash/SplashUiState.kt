package com.example.curate.presentation.splash

import androidx.annotation.DrawableRes
import com.example.curate.R
import com.example.curate.domain.model.Wallpaper

data class SplashUiState(
    val isVisible: Boolean = true,
    val durationMillis: Long = 3_000L,
    val wordmark: String = "Curate",
    val tagline: String = "discover \u00b7 collect \u00b7 share",
    val leftColumnImages: List<SplashImageUiModel> = SplashArtwork.leftColumnImages,
    val rightColumnImages: List<SplashImageUiModel> = SplashArtwork.rightColumnImages
)

data class SplashImageUiModel(
    val id: String,
    val remoteUrl: String?,
    @param:DrawableRes val fallbackImageResId: Int,
    val blurHash: String?,
    val contentDescription: String?
)

private object SplashArtwork {
    val fallbackImageResIds = listOf(
        R.drawable.splash_art_gallery,
        R.drawable.splash_art_landscape,
        R.drawable.splash_art_violet,
        R.drawable.splash_art_sun,
        R.drawable.splash_art_botanical,
        R.drawable.splash_art_ocean,
        R.drawable.splash_art_mountain,
        R.drawable.splash_art_ember
    )

    val fallbackImages = fallbackImageResIds.mapIndexed { index, fallbackImageResId ->
        SplashImageUiModel(
            id = "fallback-$index",
            remoteUrl = null,
            fallbackImageResId = fallbackImageResId,
            blurHash = null,
            contentDescription = null
        )
    }

    val leftColumnImages = fallbackImages.take(4)
    val rightColumnImages = fallbackImages.drop(4)
}

fun List<Wallpaper>.toSplashColumns(): Pair<List<SplashImageUiModel>, List<SplashImageUiModel>> {
    val remoteImages = take(SplashArtwork.fallbackImageResIds.size).mapIndexed { index, wallpaper ->
        SplashImageUiModel(
            id = wallpaper.id,
            remoteUrl = wallpaper.previewUrl.ifBlank { null },
            fallbackImageResId = SplashArtwork.fallbackImageResIds[index],
            blurHash = wallpaper.blurHash,
            contentDescription = "Photo by ${wallpaper.photographerName}"
        )
    }

    if (remoteImages.size < SplashArtwork.fallbackImageResIds.size) {
        return SplashArtwork.leftColumnImages to SplashArtwork.rightColumnImages
    }

    return remoteImages.take(4) to remoteImages.drop(4)
}
