package com.example.curate.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun StaggeredGridItem(
    itemKey: String,
    index: Int,
    shouldAnimate: Boolean,
    onAnimationCompleted: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var entered by remember(itemKey) { mutableStateOf(!shouldAnimate) }
    val shouldAnimateOnEntry = remember(itemKey) { shouldAnimate }
    val currentOnAnimationCompleted by rememberUpdatedState(onAnimationCompleted)
    val offsetPx = with(LocalDensity.current) { 32.dp.toPx() }
    val alpha = animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(
            durationMillis = 360,
            easing = LinearOutSlowInEasing
        ),
        label = "gridItemAlpha"
    )
    val progress = animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(
            durationMillis = 420,
            easing = FastOutSlowInEasing
        ),
        label = "gridItemProgress"
    )

    LaunchedEffect(itemKey) {
        if (shouldAnimateOnEntry) {
            delay((index % 12) * 55L)
        }
        entered = true
        if (shouldAnimateOnEntry) {
            currentOnAnimationCompleted(itemKey)
        }
    }

    Box(
        modifier = modifier.graphicsLayer {
            val a = alpha.value
            val p = progress.value
            this.alpha = a
            translationY = (1f - p) * offsetPx
            scaleX = 0.92f + (0.08f * p)
            scaleY = 0.92f + (0.08f * p)
        }
    ) {
        content()
    }
}
