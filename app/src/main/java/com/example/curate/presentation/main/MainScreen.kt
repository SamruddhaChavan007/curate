package com.example.curate.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.curate.ui.theme.CurateTheme

@Composable
fun MainRoute(
    viewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    MainScreen(uiState = uiState)
}

@Composable
fun MainScreen(
    uiState: MainUiState,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = uiState.title,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = uiState.subtitle,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    CurateTheme {
        MainScreen(
            uiState = MainUiState(
                title = "Curate",
                subtitle = "MVVM foundation is ready."
            )
        )
    }
}
