package com.example.curate.presentation.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.request.ImageRequest
import com.example.curate.presentation.components.CurateWallpaperImage
import com.example.curate.presentation.components.CurateLoadingContent
import com.example.curate.presentation.components.CurateMessageContent
import com.example.curate.presentation.home.WallpaperUiModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
    val transitionShape = RoundedCornerShape(8.dp)
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val dismissThresholdPx = with(density) { DragDismissThreshold.toPx() }
    val dragOffsetY = remember { Animatable(0f) }
    var isDismissRequested by remember { mutableStateOf(false) }
    val dismissProgress = (dragOffsetY.value / dismissThresholdPx).coerceIn(0f, 1f)
    val contentScale = 1f - (dismissProgress * DragDismissScaleRange)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 1f - dismissProgress * DragDismissBackgroundFade))
            .pointerInput(onBackClick, dismissThresholdPx) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            val nextOffset = (dragOffsetY.value + dragAmount).coerceAtLeast(0f)
                            dragOffsetY.snapTo(nextOffset)
                        }
                    },
                    onDragEnd = {
                        if (dragOffsetY.value >= dismissThresholdPx && !isDismissRequested) {
                            isDismissRequested = true
                            onBackClick()
                        } else {
                            coroutineScope.launch {
                                dragOffsetY.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    )
                                )
                            }
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch {
                            dragOffsetY.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            )
                        }
                    }
                )
            }
    ) {
        with(sharedTransitionScope) {
            CurateWallpaperImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(wallpaper.fullUrl)
                    .placeholderMemoryCacheKey(wallpaper.previewUrl)
                    .build(),
                contentDescription = "Wallpaper by ${wallpaper.photographerName}",
                blurHash = wallpaper.blurHash,
                contentScale = ContentScale.Crop,
                fallbackColor = Color.Black,
                fadeInImage = false,
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(x = 0, y = dragOffsetY.value.roundToInt()) }
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = "wallpaper-image-${wallpaper.id}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        clipInOverlayDuringTransition = OverlayClip(transitionShape)
                    )
                    .graphicsLayer {
                        scaleX = contentScale
                        scaleY = contentScale
                    }
            )
        }

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 8.dp, top = 8.dp)
                .graphicsLayer {
                    alpha = if (isDismissRequested) 0f else 1f - dismissProgress
                }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Gray
            )
        }
    }
}

private val DragDismissThreshold = 140.dp
private const val DragDismissScaleRange = 0.08f
private const val DragDismissBackgroundFade = 0.65f
