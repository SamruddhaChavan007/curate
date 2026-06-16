package com.example.curate.presentation.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CurateNavigationChromeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CurateNavigationChromeUiState())
    val uiState: StateFlow<CurateNavigationChromeUiState> = _uiState.asStateFlow()

    fun onContentScroll(deltaY: Float) {
        when {
            deltaY < 0f -> hideBottomBar()
            deltaY > 0f -> showBottomBar()
        }
    }

    fun showBottomBar() {
        _uiState.update { currentState ->
            currentState.copy(isBottomBarVisible = true)
        }
    }

    fun hideBottomBar() {
        _uiState.update { currentState ->
            currentState.copy(isBottomBarVisible = false)
        }
    }
}
