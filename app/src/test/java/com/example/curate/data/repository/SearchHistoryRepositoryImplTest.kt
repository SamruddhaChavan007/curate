package com.example.curate.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchHistoryRepositoryImplTest {
    @Test
    fun `record search stores newest first`() = runTest {
        val repository = createRepository()

        repository.recordSearch("forest")
        repository.recordSearch("city")

        assertEquals(listOf("city", "forest"), repository.observeRecentSearches().first().map { it.query })
    }

    @Test
    fun `record search trims and deduplicates exact query`() = runTest {
        val repository = createRepository()

        repository.recordSearch("city")
        repository.recordSearch(" city ")

        assertEquals(listOf("city"), repository.observeRecentSearches().first().map { it.query })
    }

    @Test
    fun `record search caps recents at ten`() = runTest {
        val repository = createRepository()

        repeat(12) { index ->
            repository.recordSearch("query-$index")
        }

        assertEquals(
            (11 downTo 2).map { "query-$it" },
            repository.observeRecentSearches().first().map { it.query }
        )
    }

    @Test
    fun `delete search removes matching trimmed query`() = runTest {
        val repository = createRepository()

        repository.recordSearch("forest")
        repository.recordSearch("city")
        repository.deleteSearch(" city ")

        assertEquals(listOf("forest"), repository.observeRecentSearches().first().map { it.query })
    }

    @Test
    fun `clear searches removes all recents`() = runTest {
        val repository = createRepository()

        repository.recordSearch("forest")
        repository.recordSearch("city")
        repository.clearSearches()

        assertEquals(emptyList<String>(), repository.observeRecentSearches().first().map { it.query })
    }

    private fun createRepository(): SearchHistoryRepositoryImpl {
        return SearchHistoryRepositoryImpl(InMemoryPreferencesDataStore())
    }

    private class InMemoryPreferencesDataStore : DataStore<Preferences> {
        private val state = MutableStateFlow<Preferences>(emptyPreferences())

        override val data: Flow<Preferences> = state

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            val nextPreferences = transform(state.value)
            state.value = nextPreferences
            return nextPreferences
        }
    }
}
