package com.example.curate.data.repository

import com.example.curate.domain.model.HomeMessage
import com.example.curate.domain.repository.CurateRepository
import javax.inject.Inject

class CurateRepositoryImpl @Inject constructor() : CurateRepository {
    override fun getHomeMessage(): HomeMessage {
        return HomeMessage(title = "Curate", subtitle = "MVVM foundation is ready.")
    }
}
