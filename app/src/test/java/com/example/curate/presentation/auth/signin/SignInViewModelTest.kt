package com.example.curate.presentation.auth.signin

import com.example.curate.domain.model.AuthState
import com.example.curate.domain.model.AppError
import com.example.curate.domain.model.AppErrorMessages
import com.example.curate.domain.model.AppErrorType
import com.example.curate.domain.model.AppException
import com.example.curate.domain.repository.AuthRepository
import com.example.curate.domain.repository.NetworkMonitor
import com.example.curate.domain.usecase.IsNetworkAvailableUseCase
import com.example.curate.domain.usecase.SignInUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invalid email and password prevent sign in call`() = runTest(dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = createViewModel(repository)

        viewModel.onEmailChange("bad")
        viewModel.onPasswordChange("123")
        viewModel.onSubmit()
        advanceUntilIdle()

        assertFalse(repository.signInCalled)
        assertEquals("Enter a valid email.", viewModel.uiState.value.emailError)
        assertEquals("Password must be at least 6 characters.", viewModel.uiState.value.passwordError)
    }

    @Test
    fun `successful sign in marks screen signed in`() = runTest(dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = createViewModel(repository)

        viewModel.onEmailChange("sam@example.com")
        viewModel.onPasswordChange("password1!")
        viewModel.onSubmit()
        advanceUntilIdle()

        assertTrue(repository.signInCalled)
        assertTrue(viewModel.uiState.value.isSignedIn)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `offline sign in prevents repository call`() = runTest(dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = createViewModel(repository, isOnline = false)

        viewModel.onEmailChange("sam@example.com")
        viewModel.onPasswordChange("password1!")
        viewModel.onSubmit()
        advanceUntilIdle()

        assertFalse(repository.signInCalled)
        assertEquals(AppErrorMessages.NETWORK_UNAVAILABLE, viewModel.uiState.value.submitError)
    }

    @Test
    fun `sign in failure surfaces mapped submit error`() = runTest(dispatcher) {
        val repository = FakeAuthRepository(
            signInError = AppException(
                AppError(
                    type = AppErrorType.Auth,
                    userMessage = "Email or password is incorrect.",
                    statusCode = 400
                )
            )
        )
        val viewModel = createViewModel(repository)

        viewModel.onEmailChange("sam@example.com")
        viewModel.onPasswordChange("password1!")
        viewModel.onSubmit()
        advanceUntilIdle()

        assertEquals("Email or password is incorrect.", viewModel.uiState.value.submitError)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `raw sign in failure does not surface raw message`() = runTest(dispatcher) {
        val repository = FakeAuthRepository(signInError = IllegalStateException("URL: https://example.com"))
        val viewModel = createViewModel(repository)

        viewModel.onEmailChange("sam@example.com")
        viewModel.onPasswordChange("password1!")
        viewModel.onSubmit()
        advanceUntilIdle()

        assertEquals("Unable to sign in.", viewModel.uiState.value.submitError)
    }

    private fun createViewModel(
        repository: FakeAuthRepository,
        isOnline: Boolean = true
    ): SignInViewModel {
        return SignInViewModel(
            signIn = SignInUseCase(repository),
            isNetworkAvailable = IsNetworkAvailableUseCase(FakeNetworkMonitor(isOnline))
        )
    }

    private class FakeAuthRepository(
        private val signInError: Throwable? = null
    ) : AuthRepository {
        override val authState: StateFlow<AuthState> = MutableStateFlow(AuthState.Unauthenticated)
        var signInCalled = false
            private set

        override fun isSupabaseConfigured(): Boolean = true

        override suspend fun signIn(email: String, password: String) {
            signInCalled = true
            signInError?.let { throw it }
        }

        override suspend fun signUp(name: String, email: String, password: String) = Unit
        override suspend fun signOut() = Unit
        override suspend fun refreshSession() = Unit
    }

    private class FakeNetworkMonitor(
        private val isOnline: Boolean
    ) : NetworkMonitor {
        override fun isOnline(): Boolean = isOnline
    }
}
