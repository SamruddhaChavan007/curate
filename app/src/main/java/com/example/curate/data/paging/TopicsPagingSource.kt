package com.example.curate.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.curate.data.error.AppErrorMapper
import com.example.curate.data.remote.unsplash.UnsplashApi
import com.example.curate.data.remote.unsplash.mapper.toDomain
import com.example.curate.domain.model.TopicsCategory
import timber.log.Timber

class TopicsPagingSource(
    private val api: UnsplashApi
) : PagingSource<Int, TopicsCategory>() {
    private val seenTopicIds = mutableSetOf<String>()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TopicsCategory> {
        val page = params.key ?: FIRST_PAGE

        return try {
            val topics = api.getTopics(
                page = page,
                perPage = params.loadSize.coerceAtMost(MAX_PAGE_SIZE)
            ).map { topic -> topic.toDomain() }
            val uniqueTopics = topics.filter { topic ->
                seenTopicIds.add(topic.id)
            }

            LoadResult.Page(
                data = uniqueTopics,
                prevKey = if (page == FIRST_PAGE) null else page - 1,
                nextKey = if (topics.isEmpty()) null else page + 1
            )
        } catch (error: Exception) {
            Timber.e(error, "Unable to load topics")
            LoadResult.Error(AppErrorMapper.toException(error))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TopicsCategory>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val FIRST_PAGE = 1
        const val MAX_PAGE_SIZE = 30
    }
}