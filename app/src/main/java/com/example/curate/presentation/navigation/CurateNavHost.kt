package com.example.curate.presentation.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.curate.presentation.detail.WallpaperDetailScreen
import com.example.curate.presentation.home.HomeRoute
import com.example.curate.presentation.home.HomeViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CurateNavHost(
    homeViewModel: HomeViewModel
) {
    val navController = rememberNavController()
    val selectedWallpaper by homeViewModel.selectedWallpaper.collectAsState()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Routes.HOME
        ) {
            composable(Routes.HOME) {
                HomeRoute(
                    viewModel = homeViewModel,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    onWallpaperClick = { wallpaper ->
                        homeViewModel.selectWallpaper(wallpaper)
                        navController.navigate(Routes.wallpaperDetail(wallpaper.id))
                    }
                )
            }

            composable(
                route = Routes.WALLPAPER_DETAIL,
                arguments = listOf(navArgument(Routes.WALLPAPER_ID_ARG) { type = NavType.StringType })
            ) {
                WallpaperDetailScreen(
                    wallpaper = selectedWallpaper,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    onBackClick = navController::navigateUp
                )
            }
        }
    }
}

private object Routes {
    const val HOME = "home"
    const val WALLPAPER_ID_ARG = "wallpaperId"
    const val WALLPAPER_DETAIL = "wallpaper/{$WALLPAPER_ID_ARG}"

    fun wallpaperDetail(wallpaperId: String): String = "wallpaper/$wallpaperId"
}
