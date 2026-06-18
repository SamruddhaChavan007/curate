package com.example.curate.presentation.search

import com.example.curate.domain.model.RecentSearch

data class SearchUiState(
    val query: String = "",
    val activeSearchQuery: String = "",
    val recentSearches: List<RecentSearch> = emptyList(),
    val isTopBarVisible: Boolean = true,
    val shouldAnimateGridItems: Boolean = true,
    val animatedGridItemIds: Set<String> = emptySet()
)

enum class SearchScrollDirection {
    Up,
    Down
}
