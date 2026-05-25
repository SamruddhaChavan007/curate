package com.example.curate.presentation.discover

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.curate.presentation.components.AnimatedScreenContent
import com.example.curate.presentation.components.CurateMessageContent

@Composable
fun DiscoverRoute(
    modifier: Modifier = Modifier
) {
    DiscoverScreen(modifier = modifier)
}

@Composable
fun DiscoverScreen(
    modifier: Modifier = Modifier
) {
    AnimatedScreenContent(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        CurateMessageContent(
            message = "Collections like Amoled and Nature will appear here.",
            modifier = Modifier.fillMaxSize()
        )
    }
}
