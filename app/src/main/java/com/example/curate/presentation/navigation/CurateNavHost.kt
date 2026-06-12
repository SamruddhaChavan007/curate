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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.example.curate.domain.model.AuthState
import com.example.curate.presentation.auth.AuthSessionViewModel
import com.example.curate.presentation.auth.account.AccountRoute
import com.example.curate.presentation.auth.signin.SignInRoute
import com.example.curate.presentation.auth.signup.SignUpRoute
import com.example.curate.presentation.components.CurateBottomNavigationBar
import com.example.curate.presentation.detail.WallpaperDetailRoute
import com.example.curate.presentation.discover.DiscoverRoute
import com.example.curate.presentation.home.HomeRoute
import com.example.curate.presentation.home.HomeViewModel
import com.example.curate.presentation.home.WallpaperUiModel
import com.example.curate.presentation.library.LibraryRoute
import com.example.curate.presentation.search.SearchRoute
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CurateNavHost(
    homeViewModel: HomeViewModel,
    authSessionViewModel: AuthSessionViewModel = hiltViewModel()
) {
    val navigationState = rememberSaveable(saver = CurateNavigationState.Saver) {
        CurateNavigationState()
    }
    val authState by authSessionViewModel.authState.collectAsState()
    var transitionSeedWallpaper by remember { mutableStateOf<WallpaperUiModel?>(null) }
    var isBottomBarVisible by rememberSaveable { mutableStateOf(true) }
    var previousKey by remember { mutableStateOf<CurateNavKey?>(null) }
    var isRestoringFromDetail by remember { mutableStateOf(false) }
    val currentKey = navigationState.currentKey

    SharedTransitionLayout {
        val bottomBarScrollConnection = remember(currentKey) {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    if (currentKey == CurateNavKey.Home) {
                        when {
                            available.y < 0f -> isBottomBarVisible = false
                            available.y > 0f -> isBottomBarVisible = true
                        }
                    }
                    return Offset.Zero
                }
            }
        }

        LaunchedEffect(currentKey) {
            val wasDetail = previousKey is CurateNavKey.WallpaperDetail
            val isDetail = currentKey is CurateNavKey.WallpaperDetail
            previousKey = currentKey

            if (wasDetail && !isDetail) {
                isRestoringFromDetail = true
                withFrameNanos { }
                withTimeoutOrNull(1_000L) {
                    snapshotFlow { isTransitionActive }.first { active -> !active }
                }
                isBottomBarVisible = true
                isRestoringFromDetail = false
            }
        }

        LaunchedEffect(currentKey, navigationState.selectedDestination) {
            if (currentKey !is CurateNavKey.WallpaperDetail) {
                transitionSeedWallpaper = null
            }
            if (navigationState.selectedDestination != TopLevelDestination.FEED) {
                isBottomBarVisible = true
            }
        }

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (navigationState.isAtTopLevelRoot && !isRestoringFromDetail) {
                    AnimatedVisibility(
                        visible = isBottomBarVisible,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        CurateBottomNavigationBar(
                            selectedDestination = navigationState.selectedDestination,
                            onDestinationClick = { destination ->
                                navigationState.select(destination)
                                isBottomBarVisible = true
                            }
                        )
                    }
                }
            }
        ) { _ ->
            NavDisplay(
                backStack = navigationState.currentBackStack,
                onBack = { navigationState.pop() },
                sharedTransitionScope = this@SharedTransitionLayout,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    entry<CurateNavKey.Home> {
                        HomeRoute(
                            viewModel = homeViewModel,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                            authState = authState,
                            isReturningFromDetail = isRestoringFromDetail,
                            onWallpaperClick = { wallpaper ->
                                isBottomBarVisible = false
                                transitionSeedWallpaper = wallpaper
                                navigationState.push(CurateNavKey.WallpaperDetail(wallpaper.id))
                            },
                            onAccountClick = {
                                when (authState) {
                                    is AuthState.Authenticated -> navigationState.push(CurateNavKey.Account)
                                    else -> navigationState.push(CurateNavKey.SignIn)
                                }
                            }
                        )
                    }

                    entry<CurateNavKey.Discover> {
                        DiscoverRoute()
                    }

                    entry<CurateNavKey.Search> {
                        SearchRoute()
                    }

                    entry<CurateNavKey.Library> {
                        LibraryRoute(authState = authState)
                    }

                    entry<CurateNavKey.SignIn> {
                        SignInRoute(
                            onBackClick = { navigationState.pop() },
                            onSignUpClick = { navigationState.push(CurateNavKey.SignUp) },
                            onSignedIn = { navigationState.popToRoot() }
                        )
                    }

                    entry<CurateNavKey.SignUp> {
                        SignUpRoute(
                            onBackClick = { navigationState.pop() },
                            onSignInClick = { navigationState.replaceTop(CurateNavKey.SignIn) }
                        )
                    }

                    entry<CurateNavKey.Account> {
                        AccountRoute(
                            authState = authState,
                            onBackClick = { navigationState.pop() },
                            onSignOutClick = {
                                authSessionViewModel.onSignOutClick()
                                navigationState.popToRoot()
                            }
                        )
                    }

                    entry<CurateNavKey.WallpaperDetail> { key ->
                        WallpaperDetailRoute(
                            wallpaperId = key.wallpaperId,
                            transitionSeedWallpaper = transitionSeedWallpaper,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                            onBackClick = { navigationState.pop() }
                        )
                    }
                },
                modifier = Modifier.nestedScroll(bottomBarScrollConnection)
            )
        }
    }
}

