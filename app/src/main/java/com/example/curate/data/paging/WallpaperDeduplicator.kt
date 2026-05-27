package com.example.curate.data.paging

import com.example.curate.domain.model.Wallpaper

internal class WallpaperDeduplicator {
    private val seenIds = mutableSetOf<String>()

    fun filterUnique(wallpapers: List<Wallpaper>): List<Wallpaper> {
        return wallpapers.filter { wallpaper -> seenIds.add(wallpaper.id) }
    }
}
