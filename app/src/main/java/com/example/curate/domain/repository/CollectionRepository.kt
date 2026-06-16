package com.example.curate.domain.repository

import com.example.curate.domain.model.WallpaperCategory

interface CollectionRepository {
    suspend fun fetchCategories(page: Int, perPage: Int): Result<List<WallpaperCategory>>
}