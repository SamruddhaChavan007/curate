package com.example.curate.domain.usecase

import com.example.curate.domain.repository.AuthRepository
import javax.inject.Inject

class RefreshAuthSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.refreshSession()
    }
}
