package com.example.curate.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedScreenContent(
    modifier: Modifier = Modifier,
    animateEntrance: Boolean = true,
    content: @Composable () -> Unit
) {
    var entered by remember(animateEntrance) { mutableStateOf(!animateEntrance) }
    val offsetPx = with(LocalDensity.current) { 10.dp.toPx() }
    val progress = animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "screenProgress"
    )

    LaunchedEffect(animateEntrance) {
        if (animateEntrance) {
            entered = true
        }
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                val p = progress.value
                alpha = p
                translationY = (1f - p) * offsetPx
            }
    ) {
        content()
    }
}