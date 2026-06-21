package com.example.curate.domain.usecase

import com.example.curate.domain.model.Wallpaper
import com.example.curate.domain.repository.WallpaperRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoriteWallpapersUseCase @Inject constructor(
    private val repository: WallpaperRepository
) {
    operator fun invoke(): Flow<List<Wallpaper>> =
        repository.observeFavorites()
}