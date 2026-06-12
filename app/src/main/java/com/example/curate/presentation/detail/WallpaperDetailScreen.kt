package com.example.curate.presentation.detail

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.bitmapConfig
import com.example.curate.presentation.components.CurateLoadingContent
import com.example.curate.presentation.components.CurateMessageContent
import com.example.curate.presentation.components.CurateWallpaperImage
import com.example.curate.presentation.home.WallpaperUiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WallpaperDetailRoute(
    wallpaperId: String,
    transitionSeedWallpaper: WallpaperUiModel?,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackClick: () -> Unit,
    viewModel: WallpaperDetailViewModel = hiltViewModel<WallpaperDetailViewModel, WallpaperDetailViewModel.Factory>(
        creationCallback = { factory -> factory.create(wallpaperId) }
    ),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val loadedWallpaper = (uiState as? WallpaperDetailUiState.Content)?.wallpaper
    val backButtonTint = (uiState as? WallpaperDetailUiState.Content)?.backButtonTint ?: BackButtonTint.Light
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
            backButtonTint = backButtonTint,
            onBackClick = onBackClick,
            onWallpaperImageReady = viewModel::onWallpaperImageReady,
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
    backButtonTint: BackButtonTint,
    onBackClick: () -> Unit,
    onWallpaperImageReady: (String, Bitmap, ImageBounds, ImageBounds) -> Unit,
    modifier: Modifier = Modifier
) {
    val transitionShape = RoundedCornerShape(8.dp)
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val dismissThresholdPx = with(density) { DragDismissThreshold.toPx() }
    val dragOffsetY = remember { Animatable(0f) }
    var isExitRequested by remember { mutableStateOf(false) }
    var loadedBitmap by remember(wallpaper.id) { mutableStateOf<Bitmap?>(null) }
    var imageBounds by remember(wallpaper.id) { mutableStateOf<ImageBounds?>(null) }
    var buttonBounds by remember(wallpaper.id) { mutableStateOf<ImageBounds?>(null) }
    val dismissProgress = (dragOffsetY.value / dismissThresholdPx).coerceIn(0f, 1f)
    val contentScale = 1f - (dismissProgress * DragDismissScaleRange)
    val currentOnBackClick by rememberUpdatedState(onBackClick)
    val chromeAlpha by animateFloatAsState(
        targetValue = if (isExitRequested) 0f else 1f,
        animationSpec = tween(durationMillis = DetailExitChromeFadeMillis),
        label = "DetailExitChromeAlpha"
    )
    val requestExit = remember {
        {
            if (!isExitRequested) {
                isExitRequested = true
            }
        }
    }

    BackHandler(onBack = requestExit)

    LaunchedEffect(wallpaper.id, loadedBitmap, imageBounds, buttonBounds) {
        val bitmap = loadedBitmap
        val image = imageBounds
        val button = buttonBounds
        if (bitmap != null && image != null && button != null) {
            onWallpaperImageReady(wallpaper.id, bitmap, image, button)
        }
    }

    LaunchedEffect(isExitRequested) {
        if (isExitRequested) {
            delay(DetailExitChromeFadeMillis.toLong())
            currentOnBackClick()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = chromeAlpha * (1f - dismissProgress * DragDismissBackgroundFade)))
            .pointerInput(dismissThresholdPx) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            val nextOffset = (dragOffsetY.value + dragAmount).coerceAtLeast(0f)
                            dragOffsetY.snapTo(nextOffset)
                        }
                    },
                    onDragEnd = {
                        if (dragOffsetY.value >= dismissThresholdPx && !isExitRequested) {
                            requestExit()
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
                    .allowHardware(false)
                    .bitmapConfig(Bitmap.Config.ARGB_8888)
                    .build(),
                contentDescription = "Wallpaper by ${wallpaper.photographerName}",
                blurHash = wallpaper.blurHash,
                contentScale = ContentScale.Crop,
                fallbackColor = Color.Transparent,
                fadeInImage = false,
                onBitmapLoaded = { loadedBitmap = it },
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        imageBounds = coordinates.toImageBounds()
                    }
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
            onClick = requestExit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 8.dp, top = 8.dp)
                .onGloballyPositioned { coordinates ->
                    buttonBounds = coordinates.toImageBounds()
                }
                .graphicsLayer {
                    alpha = chromeAlpha * (1f - dismissProgress)
                }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = backButtonTint.toColor()
            )
        }
    }
}

private fun BackButtonTint.toColor(): Color {
    return when (this) {
        BackButtonTint.Light -> Color.White
        BackButtonTint.Dark -> Color.Black
    }
}

private fun androidx.compose.ui.layout.LayoutCoordinates.toImageBounds(): ImageBounds {
    val position = positionInRoot()
    return ImageBounds(
        left = position.x.roundToInt(),
        top = position.y.roundToInt(),
        right = (position.x + size.width).roundToInt(),
        bottom = (position.y + size.height).roundToInt()
    )
}

private val DragDismissThreshold = 140.dp
private const val DragDismissScaleRange = 0.08f
private const val DragDismissBackgroundFade = 0.65f
private const val DetailExitChromeFadeMillis = 120
