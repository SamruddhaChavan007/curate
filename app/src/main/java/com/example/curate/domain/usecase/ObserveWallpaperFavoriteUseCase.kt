package com.example.curate.domain.usecase

import com.example.curate.domain.repository.WallpaperRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveWallpaperFavoriteUseCase @Inject constructor(
    private val repository: WallpaperRepository
) {
    operator fun invoke(wallpaperId: String): Flow<Boolean> {
        return repository.observeIsFavorite(wallpaperId)
    }
}