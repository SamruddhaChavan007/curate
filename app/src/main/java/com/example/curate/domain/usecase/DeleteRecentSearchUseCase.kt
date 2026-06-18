package com.example.curate.domain.usecase

import com.example.curate.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class DeleteRecentSearchUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(query: String) = repository.deleteSearch(query)
}
