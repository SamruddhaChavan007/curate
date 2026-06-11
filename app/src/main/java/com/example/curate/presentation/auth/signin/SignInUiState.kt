package com.example.curate.presentation.auth.signin

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val submitError: String? = null,
    val isSignedIn: Boolean = false
)
