package com.example.curate.presentation.library

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.curate.presentation.components.AnimatedScreenContent
import com.example.curate.presentation.components.CurateMessageContent

@Composable
fun LibraryRoute(
    modifier: Modifier = Modifier
) {
    LibraryScreen(modifier = modifier)
}

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier
) {
    AnimatedScreenContent(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        CurateMessageContent(
            message = "Downloaded and favourite wallpapers will appear here.",
            modifier = Modifier.fillMaxSize()
        )
    }
}
