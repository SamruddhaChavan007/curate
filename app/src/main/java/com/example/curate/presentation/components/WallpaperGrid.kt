package com.example.curate.presentation.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.curate.presentation.home.WallpaperUiModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WallpaperGrid(
    wallpapers: LazyPagingItems<WallpaperUiModel>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onWallpaperClick: (WallpaperUiModel) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier.Companion,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    shouldAnimateItems: Boolean = false,
    animatedItemIds: Set<String> = emptySet(),
    onItemAnimationCompleted: (String) -> Unit = {}
) {
    WallpaperImagePrefetcher(
        wallpapers = wallpapers,
        gridState = gridState
    )

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = gridState,
        modifier = modifier,
        contentPadding = contentPadding,
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
                    shouldAnimate = shouldAnimateItems && wallpaper.id !in animatedItemIds,
                    onAnimationCompleted = onItemAnimationCompleted
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
