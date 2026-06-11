package com.example.curate.presentation.library

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.curate.domain.model.AuthState
import com.example.curate.presentation.components.AnimatedScreenContent
import com.example.curate.presentation.components.CurateLoadingContent
import com.example.curate.presentation.components.CurateMessageContent

@Composable
fun LibraryRoute(
    authState: AuthState,
    modifier: Modifier = Modifier
) {
    LibraryScreen(
        authState = authState,
        modifier = modifier
    )
}

@Composable
fun LibraryScreen(
    authState: AuthState,
    modifier: Modifier = Modifier
) {
    AnimatedScreenContent(
        animateEntrance = false,
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        when (authState) {
            is AuthState.Authenticated -> CurateMessageContent(
                message = "Downloaded and favourite wallpapers will appear here.",
                modifier = Modifier.fillMaxSize()
            )
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
