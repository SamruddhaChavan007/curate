package com.example.curate.domain.usecase

import com.example.curate.domain.model.Wallpaper
import com.example.curate.domain.repository.WallpaperRepository
import javax.inject.Inject

class GetSplashWallpapersUseCase @Inject constructor(
    private val repository: WallpaperRepository
) {
    suspend operator fun invoke(
        query: String = WallpaperRepository.DEFAULT_QUERY,
        count: Int = WallpaperRepository.SPLASH_WALLPAPER_COUNT
    ): List<Wallpaper> {
        return repository.getSplashWallpapers(
            query = query,
            count = count
        )
    }
}
