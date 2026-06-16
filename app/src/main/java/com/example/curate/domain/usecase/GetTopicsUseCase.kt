package com.example.curate.domain.usecase

import androidx.paging.PagingData
import com.example.curate.domain.model.TopicsCategory
import com.example.curate.domain.repository.TopicsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopicsUseCase @Inject constructor(
    private val repository: TopicsRepository
) {
    operator fun invoke(): Flow<PagingData<TopicsCategory>> {
        return repository.getTopicFeed()
    }
}
