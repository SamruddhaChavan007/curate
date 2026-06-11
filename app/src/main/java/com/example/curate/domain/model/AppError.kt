package com.example.curate.domain.model

data class AppError(
    val type: AppErrorType,
    val userMessage: String,
    val statusCode: Int? = null
)

enum class AppErrorType {
    Validation,
    NetworkUnavailable,
    Auth,
    Client,
    Server,
    Unknown
}

object AppErrorMessages {
    const val NETWORK_UNAVAILABLE = "Turn on your internet connection and try again."
}

class AppException(
    val error: AppError,
    cause: Throwable? = null
) : Exception(error.userMessage, cause)

fun Throwable.safeUserMessage(fallback: String = "Something went wrong. Please try again."): String {
    return (this as? AppException)?.error?.userMessage ?: fallback
}
