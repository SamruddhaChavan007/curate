package com.example.curate.domain.repository

import com.example.curate.domain.model.AuthState
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthState>

    fun isSupabaseConfigured(): Boolean

    suspend fun signIn(email: String, password: String)

    suspend fun signUp(name: String, email: String, password: String)

    suspend fun signOut()

    suspend fun refreshSession()
}
