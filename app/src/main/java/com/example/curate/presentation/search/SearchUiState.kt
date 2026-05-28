package com.example.curate.presentation.search

data class SearchUiState(
    val query: String = "",
    val emptyMessage: String = "Search wallpapers will appear here."
)
