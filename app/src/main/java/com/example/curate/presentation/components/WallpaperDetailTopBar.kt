package com.example.curate.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WallpaperDetailTopBar(
    photographerName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        IconButton(onClick = onBackClick) {
            Text(
                text = "<",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Text(
            text = "Photo by $photographerName",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 12.dp, bottom = 10.dp)
        )
    }
}
