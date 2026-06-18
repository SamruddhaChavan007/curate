package com.example.curate.domain.usecase

import com.example.curate.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class ObserveRecentSearchesUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    operator fun invoke() = repository.observeRecentSearches()
}
