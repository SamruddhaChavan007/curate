package com.example.curate.presentation.auth

object AuthFormValidator {
    fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Enter your name."
            name.trim().length < 2 -> "Name is too short."
            else -> null
        }
    }

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Enter your email."
            !email.contains("@") || !email.contains(".") -> "Enter a valid email."
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Enter your password."
            password.length < 6 -> "Password must be at least 6 characters."
            password.none(Char::isDigit) -> "Password must include a number."
            password.none { it.isSpecialCharacter() } -> "Password must include a special character."
            else -> null
        }
    }

    private fun Char.isSpecialCharacter(): Boolean {
        return !isLetterOrDigit()
    }
}
