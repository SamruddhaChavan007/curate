package com.example.curate.domain.repository

import com.example.curate.domain.model.RecentSearch
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun observeRecentSearches(): Flow<List<RecentSearch>>

    suspend fun recordSearch(query: String)

    suspend fun deleteSearch(query: String)

    suspend fun clearSearches()
}
