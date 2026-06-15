package com.example.curate.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface CurateNavKey : NavKey {
    @Serializable
    data object Home : CurateNavKey

    @Serializable
    data object Discover : CurateNavKey

    @Serializable
    data object Search : CurateNavKey

    @Serializable
    data object Library : CurateNavKey

    @Serializable
    data class SignIn(val returnToPrevious: Boolean = false) : CurateNavKey

    @Serializable
    data class SignUp(val returnToPrevious: Boolean = false) : CurateNavKey

    @Serializable
    data object Account : CurateNavKey

    @Serializable
    data class WallpaperDetail(val wallpaperId: String) : CurateNavKey
}
