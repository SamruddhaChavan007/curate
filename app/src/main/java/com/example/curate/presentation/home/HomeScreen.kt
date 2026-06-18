package com.example.curate.presentation.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.curate.domain.model.AuthState
import com.example.curate.domain.model.safeUserMessage
import com.example.curate.presentation.components.CurateLoadingContent
import com.example.curate.presentation.components.CurateMessageContent
import com.example.curate.presentation.components.CurateTopBar
import com.example.curate.presentation.components.WallpaperGrid
import com.example.curate.ui.theme.curateColors
import kotlin.math.roundToInt

private val WallpaperGridEdgePadding = 12.dp

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    authState: AuthState,
    isTopBarVisible: Boolean,
    isReturningFromDetail: Boolean = false,
    onScrollDelta: (Float) -> Unit,
    onWallpaperClick: (WallpaperUiModel) -> Unit,
    onAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val wallpapers = viewModel.wallpapers.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(wallpapers.loadState.refresh) {
        viewModel.onInitialRefreshStateChanged(wallpapers.loadState.refresh)
    }

    HomeScreen(
        wallpapers = wallpapers,
        uiState = uiState,
        isReturningFromDetail = isReturningFromDetail,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        authState = authState,
        isTopBarVisible = isTopBarVisible,
        onScrollDelta = onScrollDelta,
        onWallpaperClick = onWallpaperClick,
        onAccountClick = onAccountClick,
        onScrollDirectionChanged = viewModel::onScrollDirectionChanged,
        onGridItemAnimationCompleted = viewModel::onGridItemAnimationCompleted,
        modifier = modifier
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    wallpapers: LazyPagingItems<WallpaperUiModel>,
    uiState: HomeUiState,
    isReturningFromDetail: Boolean = false,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    authState: AuthState,
    isTopBarVisible: Boolean,
    onScrollDelta: (Float) -> Unit,
    onWallpaperClick: (WallpaperUiModel) -> Unit,
    onAccountClick: () -> Unit,
    onScrollDirectionChanged: (HomeScrollDirection) -> Unit,
    onGridItemAnimationCompleted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyStaggeredGridState()
    val density = LocalDensity.current
    var topBarHeightPx by remember { mutableIntStateOf(0) }
    val statusBarTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val topBarHeight = if (topBarHeightPx > 0) {
        with(density) { topBarHeightPx.toDp() }
    } else {
        TopAppBarDefaults.TopAppBarExpandedHeight
    }
    val topBarOverlayHeight = statusBarTopPadding + topBarHeight
    val topBarOverlayHeightPx = with(density) { topBarOverlayHeight.toPx() }
    val animatedGridViewportTopOffsetPx by animateFloatAsState(
        targetValue = if (isTopBarVisible) topBarOverlayHeightPx else 0f,
        animationSpec = if (isReturningFromDetail) snap() else tween(durationMillis = 300),
        label = "HomeGridViewportTopOffset"
    )
    val wallpaperGridContentPadding = PaddingValues(
        start = WallpaperGridEdgePadding,
        top = WallpaperGridEdgePadding,
        end = WallpaperGridEdgePadding,
        bottom = WallpaperGridEdgePadding
    )

    ObserveHomeScrollDirection(
        gridState = gridState,
        onScrollDelta = onScrollDelta,
        onScrollDirectionChanged = onScrollDirectionChanged
    )

    Box(modifier = modifier.fillMaxSize()) {
        when (val refreshState = wallpapers.loadState.refresh) {
            is LoadState.Loading -> CurateLoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
            )

            is LoadState.Error -> CurateMessageContent(
                message = refreshState.error.safeUserMessage("Unable to load wallpapers."),
                actionLabel = "Retry",
                onAction = wallpapers::retry,
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
            )

            is LoadState.NotLoading -> {
                if (wallpapers.itemCount == 0) {
                    CurateMessageContent(
                        message = "No wallpapers found.",
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.statusBars)
                    )
                } else {
                    WallpaperGrid(
                        wallpapers = wallpapers,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onWallpaperClick = onWallpaperClick,
                        contentPadding = wallpaperGridContentPadding,
                        modifier = Modifier
                            .fillMaxSize()
                            .offset { IntOffset(0, animatedGridViewportTopOffsetPx.roundToInt()) },
                        gridState = gridState,
                        shouldAnimateItems = uiState.shouldAnimateGridItems && !isReturningFromDetail,
                        animatedItemIds = uiState.animatedGridItemIds,
                        onItemAnimationCompleted = onGridItemAnimationCompleted
                    )

                    androidx.compose.animation.AnimatedVisibility(
                        visible = isTopBarVisible,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    ) {
                        HomeTopBarOverlay(
                            statusBarTopPadding = statusBarTopPadding,
                            topBarHeight = topBarHeight,
                            accountInitial = authState.accountInitialOrNull(),
                            onAccountClick = onAccountClick,
                            onTopBarMeasured = { measuredHeightPx ->
                                topBarHeightPx = measuredHeightPx
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ObserveHomeScrollDirection(
    gridState: LazyStaggeredGridState,
    onScrollDelta: (Float) -> Unit,
    onScrollDirectionChanged: (HomeScrollDirection) -> Unit
) {
    val currentOnScrollDelta by rememberUpdatedState(onScrollDelta)
    val currentOnScrollDirectionChanged by rememberUpdatedState(onScrollDirectionChanged)

    LaunchedEffect(gridState) {
        var previousPosition: Pair<Int, Int>? = null
        var previousDirection: HomeScrollDirection? = null
        snapshotFlow {
            gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset
        }.collect { currentPosition ->
            val previous = previousPosition
            if (previous != null) {
                val currentIndex = currentPosition.first
                val currentOffset = currentPosition.second
                val previousIndex = previous.first
                val previousOffset = previous.second

                val direction = when {
                    currentIndex > previousIndex ||
                        (currentIndex == previousIndex && currentOffset > previousOffset) ->
                        HomeScrollDirection.Down

                    currentIndex < previousIndex ||
                        (currentIndex == previousIndex && currentOffset < previousOffset) ->
                        HomeScrollDirection.Up

                    else -> null
                }

                if (direction != null && direction != previousDirection) {
                    previousDirection = direction
                    currentOnScrollDelta(
                        when (direction) {
                            HomeScrollDirection.Down -> -1f
                            HomeScrollDirection.Up -> 1f
                        }
                    )
                    currentOnScrollDirectionChanged(direction)
                }
            }
            previousPosition = currentPosition
        }
    }
}

@Composable
private fun HomeTopBarOverlay(
    statusBarTopPadding: Dp,
    topBarHeight: Dp,
    accountInitial: String?,
    onAccountClick: () -> Unit,
    onTopBarMeasured: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val surface = MaterialTheme.curateColors.bg
    val topBarGradient = remember(surface) {
        Brush.verticalGradient(
            colors = listOf(
                surface.copy(alpha = 0.92f),
                surface.copy(alpha = 0.78f),
                surface.copy(alpha = 0f)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(statusBarTopPadding + topBarHeight)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(topBarGradient)
        )

        CurateTopBar(
            accountInitial = accountInitial,
            onAccountClick = onAccountClick,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .onGloballyPositioned { coordinates ->
                    onTopBarMeasured(coordinates.size.height)
                }
        )
    }
}

private fun AuthState.accountInitialOrNull(): String? {
    return when (this) {
        is AuthState.Authenticated -> {
            val source = user.displayName?.takeIf { it.isNotBlank() } ?: user.email.orEmpty()
            source.firstOrNull()?.uppercaseChar()?.toString() ?: "A"
        }
        else -> null
    }
}
