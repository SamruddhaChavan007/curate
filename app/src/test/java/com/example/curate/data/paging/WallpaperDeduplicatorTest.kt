package com.example.curate.data.paging

import com.example.curate.domain.model.Wallpaper
import org.junit.Assert.assertEquals
import org.junit.Test

class WallpaperDeduplicatorTest {
    private val deduplicator = WallpaperDeduplicator()

    @Test
    fun `filterUnique keeps first duplicate within same page`() {
        val wallpapers = listOf(
            wallpaper(id = "first", photographerName = "First"),
            wallpaper(id = "duplicate", photographerName = "Original"),
            wallpaper(id = "duplicate", photographerName = "Later"),
            wallpaper(id = "last", photographerName = "Last")
        )

        val uniqueWallpapers = deduplicator.filterUnique(wallpapers)

        assertEquals(listOf("first", "duplicate", "last"), uniqueWallpapers.map { it.id })
        assertEquals("Original", uniqueWallpapers[1].photographerName)
    }

    @Test
    fun `filterUnique drops duplicate from later page`() {
        val firstPage = listOf(
            wallpaper(id = "first"),
            wallpaper(id = "duplicate")
        )
        val secondPage = listOf(
            wallpaper(id = "duplicate"),
            wallpaper(id = "second")
        )

        deduplicator.filterUnique(firstPage)
        val uniqueSecondPage = deduplicator.filterUnique(secondPage)

        assertEquals(listOf("second"), uniqueSecondPage.map { it.id })
    }

    @Test
    fun `filterUnique preserves unique wallpapers in original order`() {
        val wallpapers = listOf(
            wallpaper(id = "one"),
            wallpaper(id = "two"),
            wallpaper(id = "three")
        )

        val uniqueWallpapers = deduplicator.filterUnique(wallpapers)

        assertEquals(listOf("one", "two", "three"), uniqueWallpapers.map { it.id })
    }

    private fun wallpaper(
        id: String,
        photographerName: String = "Photographer"
    ): Wallpaper {
        return Wallpaper(
            id = id,
            width = 1080,
            height = 1920,
            previewUrl = "https://example.com/$id-preview.jpg",
            fullUrl = "https://example.com/$id-full.jpg",
            blurHash = null,
            photographerName = photographerName,
            downloadLocation = "https://example.com/$id/download"
        )
    }
}
