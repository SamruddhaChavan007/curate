package com.example.curate.presentation.auth.signup

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val submitError: String? = null,
    val successMessage: String? = null,
    val isSignedUp: Boolean = false
)
