package com.example.curate.data.repository

import com.example.curate.data.remote.supabase.auth.SupabaseAuthGateway
import com.example.curate.data.remote.supabase.auth.SupabaseAuthSessionStatus
import com.example.curate.data.remote.supabase.auth.SupabaseAuthUser
import com.example.curate.domain.model.AuthState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImplTest {
    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)

    @Test
    fun `authenticated gateway state maps to domain auth state`() = testScope.runTest {
        val gateway = FakeSupabaseAuthGateway()
        val repository = createRepository(gateway)

        gateway.status.value = SupabaseAuthSessionStatus.Authenticated(
            SupabaseAuthUser(
                id = "user-id",
                email = "sam@example.com",
                displayName = "Sam"
            )
        )
        advanceUntilIdle()

        val authState = repository.authState.value as AuthState.Authenticated
        assertEquals("user-id", authState.user.id)
        assertEquals("sam@example.com", authState.user.email)
        assertEquals("Sam", authState.user.displayName)
    }

    @Test
    fun `config unavailable gateway state maps to domain auth state`() = testScope.runTest {
        val gateway = FakeSupabaseAuthGateway(isConfigured = false)
        val repository = createRepository(gateway)

        gateway.status.value = SupabaseAuthSessionStatus.ConfigUnavailable("Missing config.")
        advanceUntilIdle()

        assertEquals(AuthState.ConfigUnavailable("Missing config."), repository.authState.value)
        assertEquals(false, repository.isSupabaseConfigured())
    }

    @Test
    fun `repository delegates auth actions to gateway`() = testScope.runTest {
        val gateway = FakeSupabaseAuthGateway()
        val repository = createRepository(gateway)

        repository.signIn("sam@example.com", "password")
        repository.signUp("Sam", "sam@example.com", "password")
        repository.signOut()
        repository.refreshSession()

        assertTrue(gateway.signInCalled)
        assertTrue(gateway.signUpCalled)
        assertTrue(gateway.signOutCalled)
        assertTrue(gateway.refreshSessionCalled)
    }

    private fun createRepository(gateway: FakeSupabaseAuthGateway): AuthRepositoryImpl {
        return AuthRepositoryImpl(
            authGateway = gateway,
            applicationScope = CoroutineScope(dispatcher)
        )
    }

    private class FakeSupabaseAuthGateway(
        private val isConfigured: Boolean = true
    ) : SupabaseAuthGateway {
        val status = MutableStateFlow<SupabaseAuthSessionStatus>(SupabaseAuthSessionStatus.Loading)
        var signInCalled = false
            private set
        var signUpCalled = false
            private set
        var signOutCalled = false
            private set
        var refreshSessionCalled = false
            private set

        override val sessionStatus = status

        override fun isConfigured(): Boolean = isConfigured

        override suspend fun signIn(email: String, password: String) {
            signInCalled = true
        }

        override suspend fun signUp(name: String, email: String, password: String) {
            signUpCalled = true
        }

        override suspend fun signOut() {
            signOutCalled = true
        }

        override suspend fun refreshSession() {
            refreshSessionCalled = true
        }
    }
}
