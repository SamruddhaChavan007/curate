package com.example.curate.presentation.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.curate.presentation.components.AnimatedScreenContent
import com.example.curate.presentation.components.CurateLoadingContent
import com.example.curate.presentation.components.CurateMessageContent
import com.example.curate.presentation.components.StaggeredGridItem
import com.example.curate.presentation.components.TopAppSearchBar
import com.example.curate.presentation.components.WallpaperCard
import com.example.curate.presentation.components.WallpaperImagePrefetcher

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onWallpaperClick: (WallpaperUiModel) -> Unit
) {
    val wallpapers = viewModel.wallpapers.collectAsLazyPagingItems()

    HomeScreen(
        wallpapers = wallpapers,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        onWallpaperClick = onWallpaperClick
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
    AnimatedScreenContent(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopAppSearchBar() }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
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
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun WallpaperGrid(
    wallpapers: LazyPagingItems<WallpaperUiModel>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onWallpaperClick: (WallpaperUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyStaggeredGridState()
    val animatedWallpaperIds = remember { mutableStateListOf<String>() }

    WallpaperImagePrefetcher(
        wallpapers = wallpapers,
        gridState = gridState
    )

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = gridState,
        modifier = modifier,
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 10.dp
    ) {
        items(
            count = wallpapers.itemCount,
            key = { index -> wallpapers[index]?.id ?: "wallpaper-placeholder-$index" }
        ) { index ->
            val wallpaper = wallpapers[index]
            if (wallpaper != null) {
                StaggeredGridItem(
                    itemKey = wallpaper.id,
                    index = index,
                    shouldAnimate = wallpaper.id !in animatedWallpaperIds,
                    onAnimationScheduled = { itemKey -> animatedWallpaperIds += itemKey }
                ) {
                    WallpaperCard(
                        wallpaper = wallpaper,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onClick = { onWallpaperClick(wallpaper) }
                    )
                }
            }
        }

        if (wallpapers.loadState.append is LoadState.Loading) {
            item(key = "append-loading") {
                CurateLoadingContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                )
            }
        }

        val appendError = wallpapers.loadState.append as? LoadState.Error
        if (appendError != null) {
            item(key = "append-error") {
                CurateMessageContent(
                    message = appendError.error.message ?: "Unable to load more wallpapers.",
                    actionLabel = "Retry",
                    onAction = wallpapers::retry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}