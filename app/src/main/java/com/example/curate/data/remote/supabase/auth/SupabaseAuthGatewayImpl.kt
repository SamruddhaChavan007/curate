package com.example.curate.data.remote.supabase.auth

import com.example.curate.core.config.AppConfig
import com.example.curate.data.error.AppErrorMapper
import com.example.curate.di.ApplicationScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class SupabaseAuthGatewayImpl @Inject constructor(
    private val supabaseClientProvider: Provider<SupabaseClient>,
    @ApplicationScope applicationScope: CoroutineScope
) : SupabaseAuthGateway {
    override val sessionStatus: StateFlow<SupabaseAuthSessionStatus> = if (isConfigured()) {
        supabaseClientProvider.get().auth.sessionStatus
            .map { status -> status.toGatewayStatus() }
            .catch { error ->
                emit(SupabaseAuthSessionStatus.Error(AppErrorMapper.toAppError(error).userMessage))
            }
            .stateIn(
                scope = applicationScope,
                started = SharingStarted.Eagerly,
                initialValue = SupabaseAuthSessionStatus.Loading
            )
    } else {
        flowOf(SupabaseAuthSessionStatus.ConfigUnavailable(MISSING_CONFIG_MESSAGE))
            .stateIn(
                scope = applicationScope,
                started = SharingStarted.Eagerly,
                initialValue = SupabaseAuthSessionStatus.ConfigUnavailable(MISSING_CONFIG_MESSAGE)
            )
    }

    override fun isConfigured(): Boolean {
        return AppConfig.supabaseUrl.isNotBlank() && AppConfig.supabaseAnonKey.isNotBlank()
    }

    override suspend fun signIn(email: String, password: String) {
        runAuthRequest {
            authOrThrow().signInWith(Email) {
                this.email = email
                this.password = password
            }
        }
    }

    override suspend fun signUp(name: String, email: String, password: String) {
        runAuthRequest {
            authOrThrow().signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("name", name)
                }
            }
        }
    }

    override suspend fun signOut() {
        authOrThrow().signOut()
    }

    override suspend fun refreshSession() {
        authOrThrow().loadFromStorage()
    }

    private suspend fun runAuthRequest(block: suspend () -> Unit) {
        try {
            block()
        } catch (error: Exception) {
            throw AppErrorMapper.toException(error)
        }
    }

    private fun authOrThrow() = if (isConfigured()) {
        supabaseClientProvider.get().auth
    } else {
        throw SupabaseAuthUnavailableException(MISSING_CONFIG_MESSAGE)
    }

    private fun SessionStatus.toGatewayStatus(): SupabaseAuthSessionStatus {
        return when (this) {
            is SessionStatus.Authenticated -> {
                val user = session.user
                if (user != null) {
                    SupabaseAuthSessionStatus.Authenticated(user.toGatewayUser())
                } else {
                    SupabaseAuthSessionStatus.Error("Authenticated session is missing user details.")
                }
            }
            is SessionStatus.Initializing -> SupabaseAuthSessionStatus.Loading
            is SessionStatus.NotAuthenticated -> SupabaseAuthSessionStatus.Unauthenticated
            is SessionStatus.RefreshFailure -> SupabaseAuthSessionStatus.Error("Unable to refresh your session.")
        }
    }

    private fun UserInfo.toGatewayUser(): SupabaseAuthUser {
        val name = userMetadata
            ?.get("name")
            ?.jsonPrimitive
            ?.contentOrNull
        return SupabaseAuthUser(
            id = id,
            email = email,
            displayName = name
        )
    }

    private companion object {
        const val MISSING_CONFIG_MESSAGE = "Supabase authentication is not configured."
    }
}
