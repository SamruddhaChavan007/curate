package com.example.curate.domain.usecase

import com.example.curate.domain.model.HomeMessage
import com.example.curate.domain.repository.CurateRepository
import javax.inject.Inject

class GetHomeMessageUseCase @Inject constructor(
    private val repository: CurateRepository
) {
    operator fun invoke(): HomeMessage = repository.getHomeMessage()
}
