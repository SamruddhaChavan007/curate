package com.example.curate.presentation.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.curate.presentation.components.AnimatedScreenContent
import com.example.curate.presentation.components.CurateLoadingContent
import com.example.curate.presentation.components.CurateMessageContent
import com.example.curate.presentation.components.TopAppSearchBar
import com.example.curate.presentation.components.TopImageBlurScrim
import com.example.curate.presentation.components.WallpaperGrid

private val WallpaperGridEdgePadding = 12.dp
private val WallpaperGridTopSearchGap = 30.dp
private val TopImageBlurAreaHeight = 132.dp
private val TopSearchAreaFallbackHeight = 220.dp

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onWallpaperClick: (WallpaperUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val wallpapers = viewModel.wallpapers.collectAsLazyPagingItems()

    HomeScreen(
        wallpapers = wallpapers,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        onWallpaperClick = onWallpaperClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    wallpapers: LazyPagingItems<WallpaperUiModel>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onWallpaperClick: (WallpaperUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var topBarHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val topSearchAreaHeight = if (topBarHeightPx > 0) {
        with(density) { topBarHeightPx.toDp() }
    } else {
        TopSearchAreaFallbackHeight
    }
    val wallpaperGridContentPadding = PaddingValues(
        start = WallpaperGridEdgePadding,
        top = WallpaperGridEdgePadding,
        end = WallpaperGridEdgePadding,
        bottom = WallpaperGridEdgePadding
    )
    val topGridSpacerHeight = topSearchAreaHeight + WallpaperGridTopSearchGap
    val topScrimHeight = maxOf(
        TopImageBlurAreaHeight,
        topGridSpacerHeight
    )

    AnimatedScreenContent(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (val refreshState = wallpapers.loadState.refresh) {
                is LoadState.Loading -> CurateLoadingContent(modifier = Modifier.fillMaxSize())
                is LoadState.Error -> CurateMessageContent(
                    message = refreshState.error.message ?: "Unable to load wallpapers.",
                    actionLabel = "Retry",
                    onAction = wallpapers::retry,
                    modifier = Modifier.fillMaxSize()
                )

                is LoadState.NotLoading -> {
                    if (wallpapers.itemCount == 0) {
                        CurateMessageContent(
                            message = "No wallpapers found.",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        WallpaperGrid(
                            wallpapers = wallpapers,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            onWallpaperClick = onWallpaperClick,
                            contentPadding = wallpaperGridContentPadding,
                            topSpacerHeight = topGridSpacerHeight,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            TopImageBlurScrim(height = topScrimHeight)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .onGloballyPositioned { coordinates ->
                        topBarHeightPx = coordinates.size.height
                    }
            ) {
                TopAppSearchBar()
            }
        }
    }
}