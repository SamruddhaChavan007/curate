package com.example.curate.data.remote.supabase.auth

import kotlinx.coroutines.flow.StateFlow

interface SupabaseAuthGateway {
    val sessionStatus: StateFlow<SupabaseAuthSessionStatus>

    fun isConfigured(): Boolean

    suspend fun signIn(email: String, password: String)

    suspend fun signUp(name: String, email: String, password: String)

    suspend fun signOut()

    suspend fun refreshSession()
}

sealed interface SupabaseAuthSessionStatus {
    data object Loading : SupabaseAuthSessionStatus
    data object Unauthenticated : SupabaseAuthSessionStatus
    data class Authenticated(val user: SupabaseAuthUser) : SupabaseAuthSessionStatus
    data class Error(val message: String) : SupabaseAuthSessionStatus
    data class ConfigUnavailable(val message: String) : SupabaseAuthSessionStatus
}

data class SupabaseAuthUser(
    val id: String,
    val email: String?,
    val displayName: String?
)

class SupabaseAuthUnavailableException(message: String) : IllegalStateException(message)
