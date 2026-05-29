package com.example.curate.presentation.detail

data class ImageBounds(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) {
    val width: Int get() = right - left
    val height: Int get() = bottom - top

    fun isValid(): Boolean = width > 0 && height > 0
}
