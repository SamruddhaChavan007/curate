package com.example.curate.domain.usecase

import com.example.curate.domain.model.WallpaperCategory
import com.example.curate.domain.repository.CollectionRepository
import javax.inject.Inject

class GetCollectionsUseCase @Inject constructor(
    private val repository: CollectionRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        perPage: Int = 20
    ): Result<List<WallpaperCategory>> {
        return repository.fetchCategories(page, perPage)
    }
}