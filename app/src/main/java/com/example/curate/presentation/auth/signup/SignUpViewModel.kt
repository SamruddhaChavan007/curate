package com.example.curate.presentation.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.curate.domain.model.AppErrorMessages
import com.example.curate.domain.model.safeUserMessage
import com.example.curate.domain.usecase.IsNetworkAvailableUseCase
import com.example.curate.domain.usecase.SignUpUseCase
import com.example.curate.presentation.auth.AuthFormValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUp: SignUpUseCase,
    private val isNetworkAvailable: IsNetworkAvailableUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null, submitError = null) }
    }

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
        val nameError = AuthFormValidator.validateName(current.name)
        val emailError = AuthFormValidator.validateEmail(current.email)
        val passwordError = AuthFormValidator.validatePassword(current.password)
        if (nameError != null || emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    nameError = nameError,
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
            _uiState.update { it.copy(isLoading = true, submitError = null, successMessage = null) }
            runCatching {
                signUp(
                    current.name.trim(),
                    current.email.trim(),
                    current.password
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Account created. Check your email if confirmation is required."
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        submitError = error.safeUserMessage("Unable to create account.")
                    )
                }
            }
        }
    }
}
