package com.example.curate.presentation.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.curate.domain.model.AuthState
import com.example.curate.presentation.components.AnimatedScreenContent
import com.example.curate.presentation.components.CollectionCard
import com.example.curate.presentation.components.CurateLoadingContent
import com.example.curate.presentation.components.CurateMessageContent
import com.example.curate.presentation.components.CurateTopBar
import com.example.curate.presentation.components.StaggeredGridItem
import com.example.curate.ui.theme.curateColors
import kotlin.math.roundToInt

@Composable
fun DiscoverRoute(
    authState: AuthState,
    onAccountClick: () -> Unit,
    viewModel: DiscoverViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    DiscoverScreen(
        uiState = uiState,
        onRetryClick = viewModel::onRetryClick,
        onScrollDirectionChanged = viewModel::onScrollDirectionChanged,
        onGridItemAnimationCompleted = viewModel::onGridItemAnimationCompleted,
        authState = authState,
        onAccountClick = onAccountClick,
        modifier = modifier
    )
}

@Composable
fun DiscoverScreen(
    uiState: DiscoverUiState,
    onRetryClick: () -> Unit,
    onScrollDirectionChanged: (DiscoverScrollDirection) -> Unit,
    onGridItemAnimationCompleted: (String) -> Unit,
    authState: AuthState,
    onAccountClick: () -> Unit,
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
        targetValue = if (uiState.isTopBarVisible) topBarOverlayHeightPx else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "DiscoverGridViewportTopOffset"
    )

    ObserveDiscoverScrollDirection(
        gridState = gridState,
        onScrollDirectionChanged = onScrollDirectionChanged
    )

    AnimatedScreenContent(
        animateEntrance = false,
        modifier = modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> CurateLoadingContent(
                    modifier = Modifier.fillMaxSize()
                )

                uiState.errorMessage != null -> CurateMessageContent(
                    message = uiState.errorMessage,
                    actionLabel = "Retry",
                    onAction = onRetryClick,
                    modifier = Modifier.fillMaxSize()
                )

                uiState.collections.isEmpty() -> CurateMessageContent(
                    message = "No collection found",
                    modifier = Modifier.fillMaxSize()
                )

                else -> LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    state = gridState,
                    modifier = Modifier
                        .fillMaxSize()
                        .offset { IntOffset(0, animatedGridViewportTopOffsetPx.roundToInt()) },
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalItemSpacing = 10.dp
                ) {
                    itemsIndexed(
                        items = uiState.collections,
                        key = { _, collection -> collection.id }
                    ) { index, collection ->
                        StaggeredGridItem(
                            itemKey = collection.id,
                            index = index,
                            shouldAnimate = uiState.shouldAnimateGridItems &&
                                collection.id !in uiState.animatedGridItemIds,
                            onAnimationCompleted = onGridItemAnimationCompleted
                        ) {
                            CollectionCard(
                                collection = collection,
                                onClick = {}
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = uiState.isTopBarVisible,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                DiscoverTopBarOverlay(
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

@Composable
private fun DiscoverTopBarOverlay(
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
                .blur(14.dp)
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

@Composable
private fun ObserveDiscoverScrollDirection(
    gridState: LazyStaggeredGridState,
    onScrollDirectionChanged: (DiscoverScrollDirection) -> Unit
) {
    LaunchedEffect(gridState, onScrollDirectionChanged) {
        var previousPosition: Pair<Int, Int>? = null
        snapshotFlow {
            gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset
        }.collect { currentPosition ->
            val previous = previousPosition
            if (previous != null) {
                val currentIndex = currentPosition.first
                val currentOffset = currentPosition.second
                val previousIndex = previous.first
                val previousOffset = previous.second

                when {
                    currentIndex > previousIndex ||
                        (currentIndex == previousIndex && currentOffset > previousOffset) -> {
                        onScrollDirectionChanged(DiscoverScrollDirection.Down)
                    }

                    currentIndex < previousIndex ||
                        (currentIndex == previousIndex && currentOffset < previousOffset) -> {
                        onScrollDirectionChanged(DiscoverScrollDirection.Up)
                    }
                }
            }
            previousPosition = currentPosition
        }
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
