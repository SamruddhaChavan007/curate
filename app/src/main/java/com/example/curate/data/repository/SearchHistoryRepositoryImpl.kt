package com.example.curate.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.curate.domain.model.RecentSearch
import com.example.curate.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SearchHistoryRepository {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    override fun observeRecentSearches(): Flow<List<RecentSearch>> {
        return dataStore.data
            .map { preferences -> preferences[RECENT_SEARCHES_KEY].orEmpty().decodeSearches().toDomain() }
            .catch { error ->
                Timber.e(error, "Unable to read recent searches")
                emit(emptyList())
            }
    }

    override suspend fun recordSearch(query: String) {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isEmpty()) return

        dataStore.edit { preferences ->
            val currentSearches = preferences[RECENT_SEARCHES_KEY].orEmpty().decodeSearches()
            val nextSearches = (
                listOf(RecentSearchDto(normalizedQuery, System.currentTimeMillis())) +
                    currentSearches.filterNot { it.query == normalizedQuery }
                ).take(MAX_RECENT_SEARCHES)

            preferences[RECENT_SEARCHES_KEY] = nextSearches.encodeSearches()
        }
    }

    override suspend fun deleteSearch(query: String) {
        val normalizedQuery = query.trim()
        dataStore.edit { preferences ->
            val nextSearches = preferences[RECENT_SEARCHES_KEY]
                .orEmpty()
                .decodeSearches()
                .filterNot { it.query == normalizedQuery }

            preferences[RECENT_SEARCHES_KEY] = nextSearches.encodeSearches()
        }
    }

    override suspend fun clearSearches() {
        dataStore.edit { preferences ->
            preferences.remove(RECENT_SEARCHES_KEY)
        }
    }

    private fun String.decodeSearches(): List<RecentSearchDto> {
        if (isBlank()) return emptyList()

        return runCatching {
            json.decodeFromString(ListSerializer(RecentSearchDto.serializer()), this)
        }.onFailure { error ->
            Timber.e(error, "Unable to decode recent searches")
        }.getOrDefault(emptyList())
    }

    private fun List<RecentSearchDto>.encodeSearches(): String {
        return json.encodeToString(ListSerializer(RecentSearchDto.serializer()), this)
    }

    private companion object {
        const val MAX_RECENT_SEARCHES = 10
        val RECENT_SEARCHES_KEY = stringPreferencesKey("recent_searches")
    }
}

@Serializable
private data class RecentSearchDto(
    val query: String,
    val searchedAtEpochMillis: Long
)

private fun RecentSearchDto.toDomain(): RecentSearch {
    return RecentSearch(
        query = query,
        searchedAtEpochMillis = searchedAtEpochMillis
    )
}

private fun List<RecentSearchDto>.toDomain(): List<RecentSearch> {
    return map { it.toDomain() }
}
