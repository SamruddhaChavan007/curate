package com.example.curate.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.curate.presentation.home.HomeViewModel
import com.example.curate.presentation.navigation.CurateNavHost
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
                CurateNavHost(homeViewModel = homeViewModel)
            }
        }
    }
}
