package com.example.curate.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.curate.ui.theme.CurateTheme
import com.example.curate.ui.theme.PacificoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurateTopBar(
    accountInitial: String,
    onAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        windowInsets = WindowInsets(0, 0, 0, 0),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        ),
        title = {
            Text(
                text = "Curate",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = PacificoFontFamily,
                fontSize = 22.sp
            )
        },
        // Right Slot: Profile/Account Action Button
        actions = {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp, end = 16.dp)
                    .size(36.dp) // Adjusted sizing to match search field height gracefully
                    .clip(CircleShape)
                    .clickable(onClick = onAccountClick)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.LightGray, Color.Black)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = accountInitial,
                    color = MaterialTheme.colorScheme.surface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
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
fun PreviewCurateTopBar() {
    CurateTheme {
        Scaffold(
            topBar = {
                CurateTopBar(
                    accountInitial = "A",
                    onAccountClick = {}
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                // Main screen content goes here
            }
        }
    }
}
