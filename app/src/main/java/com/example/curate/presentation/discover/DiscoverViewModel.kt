package com.example.curate.presentation.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.curate.domain.model.TopicsCategory
import com.example.curate.domain.usecase.GetTopicsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    getTopics: GetTopicsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    val topics: Flow<PagingData<TopicsCategory>> = getTopics()
        .cachedIn(viewModelScope)

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
}
