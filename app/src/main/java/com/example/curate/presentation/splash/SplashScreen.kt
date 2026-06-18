package com.example.curate.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.curate.ui.theme.PacificoFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashRoute(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isVisible) {
        if (!uiState.isVisible) {
            delay(SplashExitFadeMillis.milliseconds)
            onFinished()
        }
    }

    SplashScreen(
        uiState = uiState,
        modifier = modifier
    )
}

@Composable
fun SplashScreen(
    uiState: SplashUiState,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = uiState.isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 220)),
        exit = fadeOut(animationSpec = tween(durationMillis = 420)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF050506))
        ) {
            DualColumnCarousel(
                leftColumnImages = uiState.leftColumnImages,
                rightColumnImages = uiState.rightColumnImages,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.38f))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.22f),
                                Color.Black.copy(alpha = 0.04f),
                                Color.Black.copy(alpha = 0.36f)
                            )
                        )
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 28.dp)
                    .padding(top = 48.dp)
                    .graphicsLayer { alpha = 0.98f }
            ) {
                Text(
                    text = uiState.wordmark,
                    color = Color.White,
                    fontSize = 42.sp,
                    fontFamily = PacificoFontFamily,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = uiState.tagline,
                    color = Color.White.copy(alpha = 0.62f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 3.4.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

private const val SplashExitFadeMillis = 420L

@Composable
private fun DualColumnCarousel(
    leftColumnImages: List<SplashImageUiModel>,
    rightColumnImages: List<SplashImageUiModel>,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .padding(horizontal = 16.dp)
            .graphicsLayer { alpha = 0.72f }
    ) {
        AutoScrollColumn(
            images = leftColumnImages,
            scrollUpward = true,
            modifier = Modifier.weight(1f)
        )
        AutoScrollColumn(
            images = rightColumnImages,
            scrollUpward = false,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AutoScrollColumn(
    images: List<SplashImageUiModel>,
    scrollUpward: Boolean,
    modifier: Modifier = Modifier,
    speedPx: Float = 0.62f
) {
    val repeatedImages = images + images + images
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = images.size)

    LaunchedEffect(images, scrollUpward) {
        while (isActive) {
            withFrameMillis { }
            listState.scrollBy(if (scrollUpward) speedPx else -speedPx)

            val firstVisibleIndex = listState.firstVisibleItemIndex
            if (firstVisibleIndex >= images.size * 2 || firstVisibleIndex < 1) {
                listState.scrollToItem(images.size)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            userScrollEnabled = false,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(repeatedImages) { image ->
                SplashImageTile(
                    image = image,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                )
            }
        }
        FadeEdges(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun SplashImageTile(
    image: SplashImageUiModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .graphicsLayer { alpha = 0.82f },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1C1C)
        ),
        border = BorderStroke(
            width = 0.5.dp,
            color = Color.White.copy(alpha = 0.07f)
        )
    ) {
        Image(
            painter = painterResource(image.fallbackImageResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = 0.76f
                    scaleX = 1.04f
                    scaleY = 1.04f
                }
        )
        if (image.remoteUrl != null) {
            AsyncImage(
                model = image.remoteUrl,
                contentDescription = image.contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 0.86f
                        scaleX = 1.04f
                        scaleY = 1.04f
                    }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.12f))
        )
    }
}

@Composable
private fun FadeEdges(
    modifier: Modifier = Modifier
) {
    val background = Color(0xFF050506)

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(96.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(background, Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(96.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, background)
                    )
                )
        )
    }
}
