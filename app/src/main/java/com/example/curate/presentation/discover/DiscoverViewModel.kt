package com.example.curate.presentation.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.curate.domain.model.safeUserMessage
import com.example.curate.domain.usecase.GetCollectionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val getCollections: GetCollectionsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    init {
        loadCollections()
    }

    fun onRetryClick() {
        loadCollections()
    }

    fun onScrollDirectionChanged(direction: DiscoverScrollDirection) {
        val shouldShowTopBar = direction == DiscoverScrollDirection.Up
        val shouldAnimateGridItems = direction == DiscoverScrollDirection.Down
        _uiState.update { currentState ->
            if (
                currentState.isTopBarVisible == shouldShowTopBar &&
                currentState.shouldAnimateGridItems == shouldAnimateGridItems
            ) {
                currentState
            } else {
                currentState.copy(
                    isTopBarVisible = shouldShowTopBar,
                    shouldAnimateGridItems = shouldAnimateGridItems
                )
            }
        }
    }

    fun onGridItemAnimationCompleted(itemId: String) {
        _uiState.update { currentState ->
            if (itemId in currentState.animatedGridItemIds) {
                currentState
            } else {
                currentState.copy(
                    animatedGridItemIds = currentState.animatedGridItemIds + itemId
                )
            }
        }
    }

    private fun loadCollections() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            getCollections()
                .onSuccess { collections ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            collections = collections,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.safeUserMessage("Unable to load collections.")
                        )
                    }
                }
        }
    }
}
