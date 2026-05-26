package com.example.curate.domain.usecase

import com.example.curate.domain.model.Wallpaper
import com.example.curate.domain.repository.WallpaperRepository
import javax.inject.Inject

class GetWallpaperDetailUseCase @Inject constructor(
    private val repository: WallpaperRepository
) {
    suspend operator fun invoke(id: String): Wallpaper {
        return repository.getWallpaper(id)
    }
}
