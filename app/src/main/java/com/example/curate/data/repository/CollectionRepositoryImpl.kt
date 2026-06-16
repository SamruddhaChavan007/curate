package com.example.curate.data.repository

import com.example.curate.data.remote.unsplash.UnsplashApi
import com.example.curate.data.remote.unsplash.mapper.toDomain
import com.example.curate.domain.model.WallpaperCategory
import com.example.curate.domain.repository.CollectionRepository
import javax.inject.Inject

class CollectionRepositoryImpl @Inject constructor(
    private val api: UnsplashApi,
): CollectionRepository {
    override suspend fun fetchCategories(page: Int, perPage: Int): Result<List<WallpaperCategory>> {
        return try {
            val response = api.getCollections(page, perPage)

            val domainList = response.map { dto ->
                dto.toDomain()
            }

            Result.success(domainList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}