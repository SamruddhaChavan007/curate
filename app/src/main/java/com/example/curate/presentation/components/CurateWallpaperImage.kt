package com.example.curate.presentation.components

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import coil3.BitmapImage
import coil3.Image
import coil3.toBitmap
import coil3.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CurateWallpaperImage(
    model: Any?,
    contentDescription: String?,
    blurHash: String?,
    contentScale: ContentScale,
    fallbackColor: Color,
    modifier: Modifier = Modifier,
    fadeInImage: Boolean = true,
    onBitmapLoaded: (Bitmap) -> Unit = {}
) {
    var isImageLoaded by remember(model) { mutableStateOf(false) }
    val imageAlpha by animateFloatAsState(
        targetValue = if (!fadeInImage || isImageLoaded) 1f else 0f,
        label = "WallpaperImageAlpha"
    )
    val placeholderBitmap by produceState<Bitmap?>(
        initialValue = null,
        key1 = blurHash
    ) {
        value = withContext(Dispatchers.Default) {
            BlurHashDecoder.decode(blurHash)
        }
    }

    Box(modifier = modifier.background(fallbackColor)) {
        val bitmap = placeholderBitmap
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = contentScale,
                modifier = Modifier.matchParentSize()
            )
        }

        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            contentScale = contentScale,
            onSuccess = { state ->
                isImageLoaded = true
                onBitmapLoaded(state.result.image.toReadableBitmap())
            },
            onError = { isImageLoaded = false },
            onLoading = { isImageLoaded = false },
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { alpha = imageAlpha }
        )
    }
}

private fun Image.toReadableBitmap(): Bitmap {
    return if (this is BitmapImage) {
        bitmap.toReadableBitmap()
    } else {
        toBitmap(
            width = width,
            height = height,
            config = Bitmap.Config.ARGB_8888
        )
    }
}

private fun Bitmap.toReadableBitmap(): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && config == Bitmap.Config.HARDWARE) {
        copy(Bitmap.Config.ARGB_8888, false)
    } else {
        this
    }
}
