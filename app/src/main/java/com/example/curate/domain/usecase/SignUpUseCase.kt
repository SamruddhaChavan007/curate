package com.example.curate.domain.usecase

import com.example.curate.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String) {
        authRepository.signUp(name = name, email = email, password = password)
    }
}
