package com.example.curate.domain.repository

import com.example.curate.domain.model.HomeMessage

interface CurateRepository {
    fun getHomeMessage(): HomeMessage
}
