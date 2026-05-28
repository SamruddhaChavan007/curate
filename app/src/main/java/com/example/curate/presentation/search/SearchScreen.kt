package com.example.curate.presentation.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.curate.presentation.components.CurateRecent
import com.example.curate.presentation.components.CurateSearchBar

@Composable
fun SearchRoute(
    viewModel: SearchViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    SearchScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        modifier = modifier
    )
}

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isKeyboardVisible = WindowInsets.ime.getBottom(density) > 0
    var isSearchFocused by remember { mutableStateOf(false) }
    var wasKeyboardVisibleForSearch by remember { mutableStateOf(false) }

    LaunchedEffect(isSearchFocused) {
        if (!isSearchFocused) {
            wasKeyboardVisibleForSearch = false
        }
    }

    LaunchedEffect(isKeyboardVisible, isSearchFocused) {
        if (!isSearchFocused) return@LaunchedEffect

        if (isKeyboardVisible) {
            wasKeyboardVisibleForSearch = true
        } else if (wasKeyboardVisibleForSearch) {
            focusManager.clearFocus()
        }
    }

    BackHandler(enabled = isSearchFocused) {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CurateSearchBar(
                query = uiState.query,
                onQueryChange = onSearchQueryChange,
                onFocusChange = { isSearchFocused = it }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "RECENT",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "CLEAR ALL",
                    color = Color.Gray,
                    modifier = Modifier.clickable {},
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(Modifier.height(4.dp))

            CurateRecent()
        }
    }
}