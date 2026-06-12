package com.example.curate.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.curate.ui.theme.curateColors

@Composable
fun AuthButton(
    enabled: Boolean = false,
    text: String
) {
    val curateColors = MaterialTheme.curateColors

    Button(
        onClick = {},
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
            .padding(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = curateColors.onSurface,
            contentColor = curateColors.bg,
            disabledContainerColor = curateColors.onSurface.copy(alpha = 0.32f),
            disabledContentColor = curateColors.bg.copy(alpha = 0.62f)
        )

    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (enabled) curateColors.bg else curateColors.bg.copy(alpha = 0.62f)
        )
    }
}

@Preview
@Composable
fun PreviewAuthButton() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(MaterialTheme.curateColors.bg)
        ) {
            AuthButton(
                enabled = true,
                text = "Sign Up"
            )
        }
    }
}
