package com.example.curate.presentation.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedScreenContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var entered by remember { mutableStateOf(false) }
    val offsetPx = with(LocalDensity.current) { 10.dp.toPx() }
    val alpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "screenAlpha"
    )
    val progress by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "screenProgress"
    )

    LaunchedEffect(Unit) {
        entered = true
    }

    Box(
        modifier = modifier
            .alpha(alpha)
            .graphicsLayer {
                translationY = (1f - progress) * offsetPx
            }
    ) {
        content()
    }
}
