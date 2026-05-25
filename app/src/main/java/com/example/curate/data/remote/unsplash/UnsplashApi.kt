package com.example.curate.data.remote.unsplash

import com.example.curate.core.config.AppConfig
import com.example.curate.data.remote.unsplash.dto.UnsplashSearchResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashApi @Inject constructor(
    private val httpClient: HttpClient
) {
    suspend fun searchPhotos(
        query: String,
        page: Int,
        perPage: Int
    ): UnsplashSearchResponseDto {
        check(AppConfig.unsplashAccessKey.isNotBlank()) {
            "UNSPLASH_ACCESS_KEY is missing. Set it in local.properties."
        }

        return httpClient.get(UNSPLASH_SEARCH_PHOTOS_URL) {
            header("Authorization", "Client-ID ${AppConfig.unsplashAccessKey}")
            parameter("query", query)
            parameter("orientation", "portrait")
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()
    }

    private companion object {
        const val UNSPLASH_SEARCH_PHOTOS_URL = "https://api.unsplash.com/search/photos"
    }
}
