package com.example.curate.domain.usecase

import androidx.paging.PagingData
import com.example.curate.domain.model.Wallpaper
import com.example.curate.domain.repository.WallpaperRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWallpaperFeedUseCase @Inject constructor(
    private val repository: WallpaperRepository
) {
    operator fun invoke(query: String = WallpaperRepository.DEFAULT_QUERY): Flow<PagingData<Wallpaper>> {
        return repository.getWallpaperFeed(query)
    }
}
