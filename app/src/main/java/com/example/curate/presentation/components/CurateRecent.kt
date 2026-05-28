package com.example.curate.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.curate.ui.theme.CurateTheme

@Composable
fun CurateRecent() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clickable {},
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = "Recents",
            tint = Color.LightGray,
            modifier = Modifier.size(12.dp)
        )

        Spacer(Modifier.width(4.dp))

        Text(
            text = "neon city",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = "H", // Hours
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.width(4.dp))

        IconButton(
            onClick = { }
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear Recent",
                tint = Color.LightGray,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewCurateRecent() {
    CurateTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                CurateRecent()
            }
        }
    }
}