package com.example.curate.data.repository

import com.example.curate.data.remote.supabase.auth.SupabaseAuthGateway
import com.example.curate.data.remote.supabase.auth.SupabaseAuthSessionStatus
import com.example.curate.domain.model.AuthState
import com.example.curate.domain.model.AuthUser
import com.example.curate.domain.repository.AuthRepository
import com.example.curate.di.ApplicationScope
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AuthRepositoryImpl @Inject constructor(
    private val authGateway: SupabaseAuthGateway,
    @ApplicationScope applicationScope: CoroutineScope
) : AuthRepository {
    override val authState: StateFlow<AuthState> = authGateway.sessionStatus
        .map { status -> status.toAuthState() }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.Eagerly,
            initialValue = AuthState.Loading
        )

    override fun isSupabaseConfigured(): Boolean {
        return authGateway.isConfigured()
    }

    override suspend fun signIn(email: String, password: String) {
        authGateway.signIn(email = email, password = password)
    }

    override suspend fun signUp(name: String, email: String, password: String) {
        authGateway.signUp(name = name, email = email, password = password)
    }

    override suspend fun signOut() {
        authGateway.signOut()
    }

    override suspend fun refreshSession() {
        authGateway.refreshSession()
    }

    private fun SupabaseAuthSessionStatus.toAuthState(): AuthState {
        return when (this) {
            is SupabaseAuthSessionStatus.Authenticated -> AuthState.Authenticated(
                user = AuthUser(
                    id = user.id,
                    email = user.email,
                    displayName = user.displayName
                )
            )
            is SupabaseAuthSessionStatus.ConfigUnavailable -> AuthState.ConfigUnavailable(message)
            is SupabaseAuthSessionStatus.Error -> AuthState.Error(message)
            SupabaseAuthSessionStatus.Loading -> AuthState.Loading
            SupabaseAuthSessionStatus.Unauthenticated -> AuthState.Unauthenticated
        }
    }
}
