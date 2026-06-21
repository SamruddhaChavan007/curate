package com.example.curate.presentation.library

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.curate.domain.model.AuthState
import com.example.curate.presentation.components.AnimatedScreenContent
import com.example.curate.presentation.components.CurateLoadingContent
import com.example.curate.presentation.components.CurateMessageContent
import com.example.curate.presentation.components.WallpaperCard
import com.example.curate.presentation.home.WallpaperUiModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LibraryRoute(
    authState: AuthState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onWallpaperClick: (WallpaperUiModel) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LibraryScreen(
        authState = authState,
        uiState = uiState,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        onWallpaperClick = onWallpaperClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LibraryScreen(
    authState: AuthState,
    uiState: LibraryUiState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onWallpaperClick: (WallpaperUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedScreenContent(
        animateEntrance = false,
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        when (authState) {
            is AuthState.Authenticated -> {
                if (uiState.favorites.isEmpty()) {
                    CurateMessageContent(
                        message = "No favourite wallpapers yet.",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    FavoriteWallpaperGrid(
                        wallpapers = uiState.favorites,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onWallpaperClick = onWallpaperClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            is AuthState.Loading -> CurateLoadingContent(
                modifier = Modifier.fillMaxSize()
            )

            else -> CurateMessageContent(
                message = "Sign in to use favourites and downloads.",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun FavoriteWallpaperGrid(
    wallpapers: List<WallpaperUiModel>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onWallpaperClick: (WallpaperUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 10.dp
    ) {
        items(
            items = wallpapers,
            key = { wallpaper -> wallpaper.id }
        ) { wallpaper ->
            WallpaperCard(
                wallpaper = wallpaper,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                onClick = { onWallpaperClick(wallpaper) }
            )
        }
    }
}
