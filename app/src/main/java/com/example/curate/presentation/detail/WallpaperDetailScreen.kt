package com.example.curate.presentation.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.curate.presentation.components.CurateMessageContent
import com.example.curate.presentation.components.WallpaperDetailTopBar
import com.example.curate.presentation.home.WallpaperUiModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WallpaperDetailScreen(
    wallpaper: WallpaperUiModel?,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (wallpaper == null) {
        CurateMessageContent(
            message = "Wallpaper preview is unavailable.",
            actionLabel = "Back",
            onAction = onBackClick,
            modifier = modifier.fillMaxSize()
        )
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        with(sharedTransitionScope) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(wallpaper.fullUrl)
                    .placeholderMemoryCacheKey(wallpaper.previewUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Wallpaper by ${wallpaper.photographerName}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = "wallpaper-image-${wallpaper.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )
        }

        WallpaperDetailTopBar(
            photographerName = wallpaper.photographerName,
            onBackClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
        )
    }
}
