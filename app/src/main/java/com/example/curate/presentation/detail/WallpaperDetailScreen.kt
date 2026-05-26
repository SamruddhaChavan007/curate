package com.example.curate.presentation.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.curate.presentation.components.CurateLoadingContent
import com.example.curate.presentation.components.CurateMessageContent
import com.example.curate.presentation.home.WallpaperUiModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WallpaperDetailRoute(
    transitionSeedWallpaper: WallpaperUiModel?,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackClick: () -> Unit,
    viewModel: WallpaperDetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val loadedWallpaper = (uiState as? WallpaperDetailUiState.Content)?.wallpaper
    var sharedWallpaper by remember {
        mutableStateOf(transitionSeedWallpaper ?: loadedWallpaper)
    }

    LaunchedEffect(transitionSeedWallpaper?.id, loadedWallpaper?.id) {
        if (sharedWallpaper == null) {
            sharedWallpaper = transitionSeedWallpaper ?: loadedWallpaper
        }
    }

    val wallpaper = sharedWallpaper
    if (wallpaper != null) {
        WallpaperDetailScreen(
            wallpaper = wallpaper,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            onBackClick = onBackClick,
            modifier = modifier
        )
        return
    }

    when (val state = uiState) {
        WallpaperDetailUiState.Loading -> CurateLoadingContent(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        )
        is WallpaperDetailUiState.Error -> CurateMessageContent(
            message = state.message,
            actionLabel = "Back",
            onAction = onBackClick,
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        )
        is WallpaperDetailUiState.Content -> Unit
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WallpaperDetailScreen(
    wallpaper: WallpaperUiModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 8.dp, top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Gray
            )
        }
    }
}
