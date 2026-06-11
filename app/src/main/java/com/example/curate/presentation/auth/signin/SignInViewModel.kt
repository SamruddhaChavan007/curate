package com.example.curate.presentation.auth.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.curate.domain.model.AppErrorMessages
import com.example.curate.domain.model.safeUserMessage
import com.example.curate.domain.usecase.IsNetworkAvailableUseCase
import com.example.curate.domain.usecase.SignInUseCase
import com.example.curate.presentation.auth.AuthFormValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signIn: SignInUseCase,
    private val isNetworkAvailable: IsNetworkAvailableUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, submitError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, submitError = null) }
    }

    fun onPasswordVisibilityClick() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onSubmit() {
        val current = _uiState.value
        val emailError = AuthFormValidator.validateEmail(current.email)
        val passwordError = AuthFormValidator.validatePassword(current.password)
        if (emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    submitError = null
                )
            }
            return
        }

        if (!isNetworkAvailable()) {
            _uiState.update {
                it.copy(submitError = AppErrorMessages.NETWORK_UNAVAILABLE)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, submitError = null) }
            runCatching {
                signIn(
                    current.email.trim(),
                    current.password
                )
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSignedIn = true) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        submitError = error.safeUserMessage("Unable to sign in.")
                    )
                }
            }
        }
    }
}
