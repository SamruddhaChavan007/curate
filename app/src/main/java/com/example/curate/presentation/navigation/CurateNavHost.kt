package com.example.curate.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.curate.presentation.components.CurateBottomNavigationBar
import com.example.curate.presentation.detail.WallpaperDetailScreen
import com.example.curate.presentation.discover.DiscoverRoute
import com.example.curate.presentation.home.HomeRoute
import com.example.curate.presentation.home.HomeViewModel
import com.example.curate.presentation.library.LibraryRoute

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CurateNavHost(
    homeViewModel: HomeViewModel
) {
    val navController = rememberNavController()
    val selectedWallpaper by homeViewModel.selectedWallpaper.collectAsState()

    SharedTransitionLayout {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val showBottomBar = TopLevelDestination.entries.any { destination ->
            currentDestination?.hierarchy?.any { it.route == destination.route } == true
        }
        var bottomBarVisibleByScroll by remember { mutableStateOf(true) }
        val bottomBarScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    when {
                        available.y < 0f -> bottomBarVisibleByScroll = false
                        available.y > 0f -> bottomBarVisibleByScroll = true
                    }
                    return Offset.Zero
                }
            }
        }

        LaunchedEffect(currentDestination?.route) {
            if (showBottomBar) {
                bottomBarVisibleByScroll = true
            }
        }

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (showBottomBar) {
                    AnimatedVisibility(
                        visible = bottomBarVisibleByScroll,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        CurateBottomNavigationBar(
                            currentDestinationRoute = currentDestination?.route,
                            onDestinationClick = { destination ->
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { _ ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.nestedScroll(bottomBarScrollConnection)
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

                composable(Routes.DISCOVER) {
                    DiscoverRoute()
                }

                composable(Routes.LIBRARY) {
                    LibraryRoute()
                }

                composable(
                    route = Routes.WALLPAPER_DETAIL,
                    arguments = listOf(navArgument(Routes.WALLPAPER_ID_ARG) {
                        type = NavType.StringType
                    })
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
}