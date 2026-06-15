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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.example.curate.domain.model.AuthState
import com.example.curate.presentation.components.CurateLoadingContent
import com.example.curate.presentation.components.CurateMessageContent
import com.example.curate.presentation.components.CurateWallpaperImage
import com.example.curate.presentation.home.WallpaperUiModel
import com.example.curate.ui.theme.curateColors
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
    authState: AuthState,
    onBackClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    viewModel: WallpaperDetailViewModel = hiltViewModel<WallpaperDetailViewModel, WallpaperDetailViewModel.Factory>(
        creationCallback = { factory -> factory.create(wallpaperId) }
    ),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val loadedWallpaper = (uiState as? WallpaperDetailUiState.Content)?.wallpaper
    val isFavorite = (uiState as? WallpaperDetailUiState.Content)?.isFavorite ?: false
    val isAuthenticated = authState is AuthState.Authenticated
    val backButtonTint =
        (uiState as? WallpaperDetailUiState.Content)?.backButtonTint ?: BackButtonTint.Light
    var sharedWallpaper by remember {
        mutableStateOf(transitionSeedWallpaper ?: loadedWallpaper)
    }
    var authRequiredAction by remember { mutableStateOf<AuthRequiredAction?>(null) }

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
            isFavorite = isAuthenticated && isFavorite,
            onBackClick = onBackClick,
            onFavoriteClick = {
                if (isAuthenticated) {
                    viewModel.onFavoriteClick()
                } else {
                    authRequiredAction = AuthRequiredAction.Favorite
                }
            },
            onDownloadClick = {
                if (!isAuthenticated) {
                    authRequiredAction = AuthRequiredAction.Download
                }
            },
            onWallpaperImageReady = viewModel::onWallpaperImageReady,
            modifier = modifier
        )

        authRequiredAction?.let { action ->
            AuthRequiredBottomSheet(
                action = action,
                onDismiss = { authRequiredAction = null },
                onSignInClick = {
                    authRequiredAction = null
                    onSignInClick()
                },
                onSignUpClick = {
                    authRequiredAction = null
                    onSignUpClick()
                }
            )
        }
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
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDownloadClick: () -> Unit,
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
    var bottomBarBounds by remember(wallpaper.id) { mutableStateOf<ImageBounds?>(null) }
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

        DetailBottomRow(
            chromeAlpha = chromeAlpha,
            dismissProgress = dismissProgress,
            backButtonTint = backButtonTint,
            isFavorite = isFavorite,
            onFavoriteClick = onFavoriteClick,
            onDownloadClick = onDownloadClick,
            onBoundsMeasured = { bounds ->
                bottomBarBounds = bounds
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
fun DetailBottomRow(
    onBoundsMeasured: (ImageBounds) -> Unit,
    chromeAlpha: Float,
    dismissProgress: Float,
    backButtonTint: BackButtonTint,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(6.dp)
            .onGloballyPositioned { coordinates ->
                onBoundsMeasured(coordinates.toImageBounds())
            },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier
                .padding(start = 8.dp, bottom = 8.dp)
                .graphicsLayer {
                    alpha = chromeAlpha * (1f - dismissProgress)
                }
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favourites",
                tint = backButtonTint.toColor()
            )
        }

        IconButton(
            onClick = { },
            modifier = Modifier
                .padding(bottom = 8.dp)
                .graphicsLayer {
                    alpha = chromeAlpha * (1f - dismissProgress)
                }
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Information",
                tint = backButtonTint.toColor()
            )
        }

        IconButton(
            onClick = onDownloadClick,
            modifier = Modifier
                .padding(end = 8.dp, bottom = 8.dp)
                .graphicsLayer {
                    alpha = chromeAlpha * (1f - dismissProgress)
                }
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Download",
                tint = backButtonTint.toColor()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthRequiredBottomSheet(
    action: AuthRequiredAction,
    onDismiss: () -> Unit,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    val curateColors = MaterialTheme.curateColors

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = action.title,
                style = MaterialTheme.typography.titleLarge,
                color = curateColors.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = action.message,
                style = MaterialTheme.typography.bodyMedium,
                color = curateColors.onSubtle
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onSignInClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = curateColors.onSurface,
                    contentColor = curateColors.bg
                )
            ) {
                Text("Sign in")
            }
            OutlinedButton(
                onClick = onSignUpClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Create account")
            }
        }
    }
}

enum class AuthRequiredAction(
    val title: String,
    val message: String
) {
    Favorite(
        title = "Sign in to save favourites",
        message = "Sign in or create an account to mark wallpapers as favourite."
    ),
    Download(
        title = "Sign in to download",
        message = "Sign in or create an account to download wallpapers."
    )
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
