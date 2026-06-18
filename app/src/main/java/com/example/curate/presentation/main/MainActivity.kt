package com.example.curate.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.curate.presentation.home.HomeViewModel
import com.example.curate.presentation.navigation.CurateNavHost
import com.example.curate.presentation.splash.SplashRoute
import com.example.curate.ui.theme.CurateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CurateTheme {
                CurateAppRoot(homeViewModel = homeViewModel)
            }
        }
    }
}

@Composable
private fun CurateAppRoot(
    homeViewModel: HomeViewModel
) {
    var showSplash by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        CurateNavHost(homeViewModel = homeViewModel)

        if (showSplash) {
            SplashRoute(
                onFinished = { showSplash = false },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
