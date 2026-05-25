package com.example.curate.presentation.components

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.paging.compose.LazyPagingItems
import coil3.imageLoader
import coil3.request.ImageRequest
import com.example.curate.presentation.home.WallpaperUiModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun WallpaperImagePrefetcher(
    wallpapers: LazyPagingItems<WallpaperUiModel>,
    gridState: LazyStaggeredGridState,
    prefetchWindow: Int = 8
) {
    val context = LocalContext.current
    val prefetchedUrls = remember { mutableSetOf<String>() }

    LaunchedEffect(wallpapers, gridState, context, prefetchWindow) {
        snapshotFlow {
            val visibleIndexes = gridState.layoutInfo.visibleItemsInfo.map { item -> item.index }
            val lastVisibleIndex = visibleIndexes.maxOrNull() ?: -1
            val itemCount = wallpapers.itemCount

            if (lastVisibleIndex < 0 || itemCount == 0) {
                emptyList()
            } else {
                val endIndex = minOf(itemCount - 1, lastVisibleIndex + prefetchWindow)
                (0..endIndex).mapNotNull { index -> wallpapers.peek(index)?.previewUrl }
            }
        }
            .map { urls -> urls.filterNot { url -> url in prefetchedUrls } }
            .distinctUntilChanged()
            .collectLatest { urls ->
                urls.forEach { url ->
                    prefetchedUrls += url
                    context.imageLoader.enqueue(
                        ImageRequest.Builder(context)
                            .data(url)
                            .build()
                    )
                }
            }
    }
}
