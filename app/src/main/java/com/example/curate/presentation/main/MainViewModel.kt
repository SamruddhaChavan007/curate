package com.example.curate.presentation.main

import androidx.lifecycle.ViewModel
import com.example.curate.domain.usecase.GetHomeMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

@HiltViewModel
class MainViewModel @Inject constructor(
    getHomeMessage: GetHomeMessageUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        val message = getHomeMessage()
        _uiState.value = MainUiState(
            title = message.title,
            subtitle = message.subtitle
        )
        Timber.d("MainViewModel initialized")
    }
}
