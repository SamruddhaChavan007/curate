package com.example.curate.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.curate.ui.theme.PacificoFontFamily

@Composable
fun AuthTopBar() {
    Text(
        text = "Curate",
        fontFamily = PacificoFontFamily,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onSurface
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Spacer(Modifier.width(4.dp))
        Text(
            text = "Curate Your Experience",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.width(4.dp))
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}