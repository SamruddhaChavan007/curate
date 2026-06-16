package com.example.curate.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.curate.data.paging.TopicsPagingSource
import com.example.curate.data.remote.unsplash.UnsplashApi
import com.example.curate.domain.model.TopicsCategory
import com.example.curate.domain.repository.TopicsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TopicsRepositoryImpl @Inject constructor(
    private val api: UnsplashApi,
): TopicsRepository {
    override fun getTopicFeed(): Flow<PagingData<TopicsCategory>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                TopicsPagingSource(api = api)
            }
        ).flow
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
