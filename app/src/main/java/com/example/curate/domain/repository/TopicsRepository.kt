package com.example.curate.domain.repository

import androidx.paging.PagingData
import com.example.curate.domain.model.TopicsCategory
import kotlinx.coroutines.flow.Flow

interface TopicsRepository {
    fun getTopicFeed(): Flow<PagingData<TopicsCategory>>
}
