package com.example.curate.presentation.search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.curate.domain.model.safeUserMessage
import com.example.curate.presentation.components.CurateLoadingContent
import com.example.curate.presentation.components.CurateMessageContent
import com.example.curate.presentation.components.CurateRecent
import com.example.curate.presentation.components.CurateSearchBar
import com.example.curate.presentation.components.WallpaperGrid
import com.example.curate.presentation.home.WallpaperUiModel
import com.example.curate.ui.theme.curateColors

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchRoute(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isTopBarVisible: Boolean,
    isReturningFromDetail: Boolean,
    onScrollDelta: (Float) -> Unit,
    onWallpaperClick: (WallpaperUiModel) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val wallpapers = viewModel.wallpapers.collectAsLazyPagingItems()

    SearchScreen(
        uiState = uiState,
        wallpapers = wallpapers,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        isTopBarVisible = isTopBarVisible,
        isReturningFromDetail = isReturningFromDetail,
        onScrollDelta = onScrollDelta,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onRecentSearchClick = viewModel::onRecentSearchClick,
        onDeleteRecentSearch = viewModel::onDeleteRecentSearch,
        onClearRecentSearches = viewModel::onClearRecentSearches,
        onWallpaperClick = onWallpaperClick,
        onScrollDirectionChanged = viewModel::onScrollDirectionChanged,
        onGridItemAnimationCompleted = viewModel::onGridItemAnimationCompleted,
        onSearchSubmitted = viewModel::onSearchSubmitted,
        modifier = modifier
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchScreen(
    uiState: SearchUiState,
    wallpapers: LazyPagingItems<WallpaperUiModel>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isTopBarVisible: Boolean,
    isReturningFromDetail: Boolean,
    onScrollDelta: (Float) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onRecentSearchClick: (String) -> Unit,
    onDeleteRecentSearch: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onWallpaperClick: (WallpaperUiModel) -> Unit,
    onScrollDirectionChanged: (SearchScrollDirection) -> Unit,
    onGridItemAnimationCompleted: (String) -> Unit,
    onSearchSubmitted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isKeyboardVisible = WindowInsets.ime.getBottom(density) > 0
    var isSearchFocused by remember { mutableStateOf(false) }
    var wasKeyboardVisibleForSearch by remember { mutableStateOf(false) }
    val gridState = rememberLazyStaggeredGridState()
    val isQueryBlank = uiState.query.trim().isBlank()
    val isWaitingForDebouncedQuery = !isQueryBlank && uiState.query.trim() != uiState.activeSearchQuery

    LaunchedEffect(isSearchFocused) {
        if (!isSearchFocused) {
            wasKeyboardVisibleForSearch = false
        }
    }

    LaunchedEffect(isKeyboardVisible, isSearchFocused) {
        if (!isSearchFocused) return@LaunchedEffect

        if (isKeyboardVisible) {
            wasKeyboardVisibleForSearch = true
        } else if (wasKeyboardVisibleForSearch) {
            focusManager.clearFocus()
        }
    }

    BackHandler(enabled = isSearchFocused) {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    ObserveSearchScrollDirection(
        gridState = gridState,
        onScrollDelta = onScrollDelta,
        onScrollDirectionChanged = onScrollDirectionChanged
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            AnimatedVisibility(
                visible = isTopBarVisible,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
            ) {
                CurateSearchBar(
                    query = uiState.query,
                    onQueryChange = onSearchQueryChange,
                    onFocusChange = { isSearchFocused = it },
                    onSearch = onSearchSubmitted
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isQueryBlank) {
                RecentSearchContent(
                    uiState = uiState,
                    onRecentSearchClick = onRecentSearchClick,
                    onDeleteRecentSearch = onDeleteRecentSearch,
                    onClearRecentSearches = onClearRecentSearches,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                SearchResultsContent(
                    wallpapers = wallpapers,
                    query = uiState.activeSearchQuery,
                    isWaitingForDebouncedQuery = isWaitingForDebouncedQuery,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    isReturningFromDetail = isReturningFromDetail,
                    onWallpaperClick = onWallpaperClick,
                    gridState = gridState,
                    shouldAnimateItems = uiState.shouldAnimateGridItems && !isReturningFromDetail,
                    animatedItemIds = uiState.animatedGridItemIds,
                    onGridItemAnimationCompleted = onGridItemAnimationCompleted,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun RecentSearchContent(
    uiState: SearchUiState,
    onRecentSearchClick: (String) -> Unit,
    onDeleteRecentSearch: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    modifier: Modifier = Modifier
) {
    val curateColors = MaterialTheme.curateColors

    Column(
        modifier = modifier.padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "RECENT",
                color = curateColors.onFaint,
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 2.sp
            )

            if (uiState.recentSearches.isNotEmpty()) {
                Text(
                    text = "CLEAR ALL",
                    color = curateColors.onSubtle,
                    modifier = Modifier
                        .clickable(onClick = onClearRecentSearches)
                        .padding(8.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        if (uiState.recentSearches.isEmpty()) {
            CurateMessageContent(
                message = "Search wallpapers will appear here.",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            uiState.recentSearches.forEach { recentSearch ->
                CurateRecent(
                    query = recentSearch.query,
                    onClick = { onRecentSearchClick(recentSearch.query) },
                    onClearClick = { onDeleteRecentSearch(recentSearch.query) }
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SearchResultsContent(
    wallpapers: LazyPagingItems<WallpaperUiModel>,
    query: String,
    isWaitingForDebouncedQuery: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isReturningFromDetail: Boolean,
    onWallpaperClick: (WallpaperUiModel) -> Unit,
    gridState: LazyStaggeredGridState,
    shouldAnimateItems: Boolean,
    animatedItemIds: Set<String>,
    onGridItemAnimationCompleted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isWaitingForDebouncedQuery) {
        CurateLoadingContent(modifier = modifier)
        return
    }

    when (val refreshState = wallpapers.loadState.refresh) {
        is LoadState.Loading -> CurateLoadingContent(modifier = modifier)

        is LoadState.Error -> CurateMessageContent(
            message = refreshState.error.safeUserMessage("Unable to search wallpapers."),
            actionLabel = "Retry",
            onAction = wallpapers::retry,
            modifier = modifier
        )

        is LoadState.NotLoading -> {
            if (wallpapers.itemCount == 0) {
                CurateMessageContent(
                    message = "No wallpapers found for \"$query\".",
                    modifier = modifier
                )
            } else {
                WallpaperGrid(
                    wallpapers = wallpapers,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onWallpaperClick = onWallpaperClick,
                    contentPadding = PaddingValues(12.dp),
                    modifier = modifier,
                    gridState = gridState,
                    shouldAnimateItems = shouldAnimateItems,
                    animatedItemIds = animatedItemIds,
                    onItemAnimationCompleted = onGridItemAnimationCompleted
                )
            }
        }
    }
}

@Composable
private fun ObserveSearchScrollDirection(
    gridState: LazyStaggeredGridState,
    onScrollDelta: (Float) -> Unit,
    onScrollDirectionChanged: (SearchScrollDirection) -> Unit
) {
    val currentOnScrollDelta by rememberUpdatedState(onScrollDelta)
    val currentOnScrollDirectionChanged by rememberUpdatedState(onScrollDirectionChanged)

    LaunchedEffect(gridState) {
        var previousPosition: Pair<Int, Int>? = null
        var previousDirection: SearchScrollDirection? = null
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
                        SearchScrollDirection.Down

                    currentIndex < previousIndex ||
                        (currentIndex == previousIndex && currentOffset < previousOffset) ->
                        SearchScrollDirection.Up

                    else -> null
                }

                if (direction != null && direction != previousDirection) {
                    previousDirection = direction
                    currentOnScrollDelta(
                        when (direction) {
                            SearchScrollDirection.Down -> -1f
                            SearchScrollDirection.Up -> 1f
                        }
                    )
                    currentOnScrollDirectionChanged(direction)
                }
            }
            previousPosition = currentPosition
        }
    }
}
