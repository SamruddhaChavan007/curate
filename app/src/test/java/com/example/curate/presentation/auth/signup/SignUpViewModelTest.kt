package com.example.curate.presentation.auth.signup

import com.example.curate.domain.model.AuthState
import com.example.curate.domain.model.AppError
import com.example.curate.domain.model.AppErrorMessages
import com.example.curate.domain.model.AppErrorType
import com.example.curate.domain.model.AppException
import com.example.curate.domain.repository.AuthRepository
import com.example.curate.domain.repository.NetworkMonitor
import com.example.curate.domain.usecase.IsNetworkAvailableUseCase
import com.example.curate.domain.usecase.SignUpUseCase
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
class SignUpViewModelTest {
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
    fun `invalid fields prevent sign up call`() = runTest(dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = createViewModel(repository)

        viewModel.onNameChange("")
        viewModel.onEmailChange("bad")
        viewModel.onPasswordChange("123")
        viewModel.onSubmit()
        advanceUntilIdle()

        assertFalse(repository.signUpCalled)
        assertEquals("Enter your name.", viewModel.uiState.value.nameError)
        assertEquals("Enter a valid email.", viewModel.uiState.value.emailError)
        assertEquals("Password must be at least 6 characters.", viewModel.uiState.value.passwordError)
    }

    @Test
    fun `successful sign up shows success message`() = runTest(dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = createViewModel(repository)

        viewModel.onNameChange("Sam")
        viewModel.onEmailChange("sam@example.com")
        viewModel.onPasswordChange("password1!")
        viewModel.onSubmit()
        advanceUntilIdle()

        assertTrue(repository.signUpCalled)
        assertEquals(
            "Account created. Check your email if confirmation is required.",
            viewModel.uiState.value.successMessage
        )
    }

    @Test
    fun `offline sign up prevents repository call`() = runTest(dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = createViewModel(repository, isOnline = false)

        viewModel.onNameChange("Sam")
        viewModel.onEmailChange("sam@example.com")
        viewModel.onPasswordChange("password1!")
        viewModel.onSubmit()
        advanceUntilIdle()

        assertFalse(repository.signUpCalled)
        assertEquals(AppErrorMessages.NETWORK_UNAVAILABLE, viewModel.uiState.value.submitError)
    }

    @Test
    fun `sign up failure surfaces mapped submit error`() = runTest(dispatcher) {
        val repository = FakeAuthRepository(
            signUpError = AppException(
                AppError(
                    type = AppErrorType.Auth,
                    userMessage = "An account with this email already exists.",
                    statusCode = 400
                )
            )
        )
        val viewModel = createViewModel(repository)

        viewModel.onNameChange("Sam")
        viewModel.onEmailChange("sam@example.com")
        viewModel.onPasswordChange("password1!")
        viewModel.onSubmit()
        advanceUntilIdle()

        assertEquals("An account with this email already exists.", viewModel.uiState.value.submitError)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `raw sign up failure does not surface raw message`() = runTest(dispatcher) {
        val repository = FakeAuthRepository(signUpError = IllegalStateException("Headers: Authorization"))
        val viewModel = createViewModel(repository)

        viewModel.onNameChange("Sam")
        viewModel.onEmailChange("sam@example.com")
        viewModel.onPasswordChange("password1!")
        viewModel.onSubmit()
        advanceUntilIdle()

        assertEquals("Unable to create account.", viewModel.uiState.value.submitError)
    }

    private fun createViewModel(
        repository: FakeAuthRepository,
        isOnline: Boolean = true
    ): SignUpViewModel {
        return SignUpViewModel(
            signUp = SignUpUseCase(repository),
            isNetworkAvailable = IsNetworkAvailableUseCase(FakeNetworkMonitor(isOnline))
        )
    }

    private class FakeAuthRepository(
        private val signUpError: Throwable? = null
    ) : AuthRepository {
        override val authState: StateFlow<AuthState> = MutableStateFlow(AuthState.Unauthenticated)
        var signUpCalled = false
            private set

        override fun isSupabaseConfigured(): Boolean = true

        override suspend fun signIn(email: String, password: String) = Unit

        override suspend fun signUp(name: String, email: String, password: String) {
            signUpCalled = true
            signUpError?.let { throw it }
        }

        override suspend fun signOut() = Unit
        override suspend fun refreshSession() = Unit
    }

    private class FakeNetworkMonitor(
        private val isOnline: Boolean
    ) : NetworkMonitor {
        override fun isOnline(): Boolean = isOnline
    }
}
