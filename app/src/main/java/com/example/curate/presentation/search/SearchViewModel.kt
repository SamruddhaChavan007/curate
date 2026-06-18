package com.example.curate.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.curate.domain.usecase.ClearRecentSearchesUseCase
import com.example.curate.domain.usecase.DeleteRecentSearchUseCase
import com.example.curate.domain.usecase.GetWallpaperFeedUseCase
import com.example.curate.domain.usecase.ObserveRecentSearchesUseCase
import com.example.curate.domain.usecase.RecordRecentSearchUseCase
import com.example.curate.presentation.home.WallpaperUiModel
import com.example.curate.presentation.home.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getWallpaperFeed: GetWallpaperFeedUseCase,
    observeRecentSearches: ObserveRecentSearchesUseCase,
    private val recordRecentSearch: RecordRecentSearchUseCase,
    private val deleteRecentSearch: DeleteRecentSearchUseCase,
    private val clearRecentSearches: ClearRecentSearchesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    val wallpapers: Flow<PagingData<WallpaperUiModel>> = _uiState
        .map { state -> state.query.trim() }
        .debounce(SEARCH_DEBOUNCE_MILLIS.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                _uiState.update { currentState ->
                    currentState.copy(
                        activeSearchQuery = "",
                        animatedGridItemIds = emptySet()
                    )
                }
                flowOf(PagingData.empty())
            } else {
                flow {
                    _uiState.update { currentState ->
                        currentState.copy(
                            activeSearchQuery = query,
                            animatedGridItemIds = emptySet()
                        )
                    }
                    emitAll(
                        getWallpaperFeed(query)
                            .map { pagingData -> pagingData.map { wallpaper -> wallpaper.toUiModel() } }
                    )
                }
            }
        }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            observeRecentSearches().collect { recentSearches ->
                _uiState.update { currentState ->
                    currentState.copy(recentSearches = recentSearches)
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { currentState -> currentState.copy(query = query) }
    }

    fun onRecentSearchClick(query: String) {
        onSearchQueryChange(query)
    }

    fun onSearchSubmitted() {
        val query = _uiState.value.query.trim()
        if (query.isBlank()) return

        viewModelScope.launch {
            recordRecentSearch(query)
        }
    }

    fun onDeleteRecentSearch(query: String) {
        viewModelScope.launch {
            deleteRecentSearch(query)
        }
    }

    fun onClearRecentSearches() {
        viewModelScope.launch {
            clearRecentSearches()
        }
    }

    fun onScrollDirectionChanged(direction: SearchScrollDirection) {
        val shouldShowTopBar = direction == SearchScrollDirection.Up
        val shouldAnimateGridItems = direction == SearchScrollDirection.Down
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

    private companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 400L
    }
}
