package com.example.curate.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.curate.domain.model.WallpaperCategory
import com.example.curate.ui.theme.CurateTheme
import com.example.curate.ui.theme.curateColors

@Composable
fun CollectionCard(
    collection: WallpaperCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)
    val curateColors = MaterialTheme.curateColors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(shape)
            .clickable(onClick = onClick)
    ) {
        CurateWallpaperImage(
            model = collection.imageUrl,
            contentDescription = collection.title,
            blurHash = collection.blurHash,
            contentScale = ContentScale.Crop,
            fallbackColor = curateColors.chrome,
            modifier = Modifier.matchParentSize()
        )

        Surface(
            color = curateColors.darkScrim.copy(alpha = 0.56f),
            contentColor = curateColors.overlayText,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = collection.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = collection.countLabel,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewCollectionCard() {
    CurateTheme(
        dynamicColor = false
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(it)
            ) {
                CollectionCard(
                    collection = WallpaperCategory(
                        id = "1",
                        title = "Nature",
                        countLabel = "48 wallpapers",
                        imageUrl = "",
                        blurHash = null
                    ),
                    onClick = {}
                )
            }
        }
    }
}
