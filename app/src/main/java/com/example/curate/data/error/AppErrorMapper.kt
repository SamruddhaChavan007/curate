package com.example.curate.data.error

import com.example.curate.domain.model.AppError
import com.example.curate.domain.model.AppErrorMessages
import com.example.curate.domain.model.AppErrorType
import com.example.curate.domain.model.AppException
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.exception.AuthWeakPasswordException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object AppErrorMapper {
    fun toException(error: Throwable): AppException {
        return error as? AppException ?: AppException(
            error = toAppError(error),
            cause = error
        )
    }

    fun toAppError(error: Throwable): AppError {
        return when (error) {
            is AppException -> error.error
            is AuthWeakPasswordException -> AppError(
                type = AppErrorType.Auth,
                statusCode = error.statusCode,
                userMessage = "Password is too weak. Include at least 6 characters, a number, and a special character."
            )
            is AuthRestException -> mapAuthRestException(error)
            is RestException -> mapStatusCode(error.statusCode)
            is ClientRequestException -> mapStatusCode(error.response.status.value)
            is ServerResponseException -> mapStatusCode(error.response.status.value)
            is ResponseException -> mapStatusCode(error.response.status.value)
            is HttpRequestException,
            is HttpRequestTimeoutException,
            is UnknownHostException,
            is SocketTimeoutException,
            is IOException -> AppError(
                type = AppErrorType.NetworkUnavailable,
                userMessage = AppErrorMessages.NETWORK_UNAVAILABLE
            )
            else -> AppError(
                type = AppErrorType.Unknown,
                userMessage = "Something went wrong. Please try again."
            )
        }
    }

    fun mapStatusCode(statusCode: Int): AppError {
        return when (statusCode) {
            400 -> AppError(
                type = AppErrorType.Client,
                statusCode = statusCode,
                userMessage = "Something was wrong with the request."
            )
            401 -> AppError(
                type = AppErrorType.Auth,
                statusCode = statusCode,
                userMessage = "Please sign in again."
            )
            403 -> AppError(
                type = AppErrorType.Auth,
                statusCode = statusCode,
                userMessage = "You do not have permission to do that."
            )
            404 -> AppError(
                type = AppErrorType.Client,
                statusCode = statusCode,
                userMessage = "Requested content was not found."
            )
            429 -> AppError(
                type = AppErrorType.Client,
                statusCode = statusCode,
                userMessage = "Too many attempts. Please try again later."
            )
            in 500..599 -> AppError(
                type = AppErrorType.Server,
                statusCode = statusCode,
                userMessage = "Server is having trouble. Please try again later."
            )
            in 400..499 -> AppError(
                type = AppErrorType.Client,
                statusCode = statusCode,
                userMessage = "Something was wrong with the request."
            )
            else -> AppError(
                type = AppErrorType.Unknown,
                statusCode = statusCode,
                userMessage = "Something went wrong. Please try again."
            )
        }
    }

    fun mapAuthErrorCode(errorCode: AuthErrorCode?, statusCode: Int): AppError {
        return when (errorCode) {
            AuthErrorCode.InvalidCredentials -> AppError(
                type = AppErrorType.Auth,
                statusCode = statusCode,
                userMessage = "Email or password is incorrect."
            )
            AuthErrorCode.EmailExists,
            AuthErrorCode.UserAlreadyExists -> AppError(
                type = AppErrorType.Auth,
                statusCode = statusCode,
                userMessage = "An account with this email already exists."
            )
            AuthErrorCode.EmailNotConfirmed -> AppError(
                type = AppErrorType.Auth,
                statusCode = statusCode,
                userMessage = "Please confirm your email before signing in."
            )
            AuthErrorCode.SignupDisabled,
            AuthErrorCode.EmailProviderDisabled,
            AuthErrorCode.ProviderDisabled -> AppError(
                type = AppErrorType.Auth,
                statusCode = statusCode,
                userMessage = "Email authentication is not enabled for this app."
            )
            AuthErrorCode.OverRequestRateLimit,
            AuthErrorCode.OverEmailSendRateLimit,
            AuthErrorCode.OverSmsSendRateLimit -> AppError(
                type = AppErrorType.Client,
                statusCode = statusCode,
                userMessage = "Too many attempts. Please try again later."
            )
            AuthErrorCode.WeakPassword -> AppError(
                type = AppErrorType.Auth,
                statusCode = statusCode,
                userMessage = "Password is too weak. Include at least 6 characters, a number, and a special character."
            )
            else -> mapStatusCode(statusCode)
        }
    }

    private fun mapAuthRestException(error: AuthRestException): AppError {
        return mapAuthErrorCode(
            errorCode = error.errorCode,
            statusCode = error.statusCode
        )
    }
}
