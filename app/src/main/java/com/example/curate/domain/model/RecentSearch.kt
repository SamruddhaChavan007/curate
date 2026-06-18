package com.example.curate.domain.model

data class RecentSearch(
    val query: String,
    val searchedAtEpochMillis: Long
)
