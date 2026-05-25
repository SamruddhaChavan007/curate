package com.example.curate.presentation.navigation

object Routes {
    const val HOME = "home"
    const val DISCOVER = "discover"
    const val LIBRARY = "library"
    const val WALLPAPER_ID_ARG = "wallpaperId"
    const val WALLPAPER_DETAIL = "wallpaper/{$WALLPAPER_ID_ARG}"

    fun wallpaperDetail(wallpaperId: String): String = "wallpaper/$wallpaperId"
}