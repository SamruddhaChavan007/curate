package com.example.curate.presentation.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.curate.presentation.home.WallpaperUiModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WallpaperCard(
    wallpaper: WallpaperUiModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)

    with(sharedTransitionScope) {
        AsyncImage(
            model = wallpaper.previewUrl,
            contentDescription = "Wallpaper by ${wallpaper.photographerName}",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(wallpaper.aspectRatio)
                .sharedElement(
                    sharedContentState = rememberSharedContentState(key = "wallpaper-image-${wallpaper.id}"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onClick)
        )
    }
}
