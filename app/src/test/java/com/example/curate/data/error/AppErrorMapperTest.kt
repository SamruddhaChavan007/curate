package com.example.curate.data.error

import com.example.curate.domain.model.AppErrorMessages
import com.example.curate.domain.model.AppErrorType
import io.github.jan.supabase.auth.exception.AuthErrorCode
import java.net.UnknownHostException
import org.junit.Assert.assertEquals
import org.junit.Test

class AppErrorMapperTest {
    @Test
    fun `maps invalid credentials auth code`() {
        val error = AppErrorMapper.mapAuthErrorCode(AuthErrorCode.InvalidCredentials, 400)

        assertEquals(AppErrorType.Auth, error.type)
        assertEquals(400, error.statusCode)
        assertEquals("Email or password is incorrect.", error.userMessage)
    }

    @Test
    fun `maps duplicate email auth code`() {
        val error = AppErrorMapper.mapAuthErrorCode(AuthErrorCode.EmailExists, 400)

        assertEquals("An account with this email already exists.", error.userMessage)
    }

    @Test
    fun `maps common status codes`() {
        assertEquals(
            "Something was wrong with the request.",
            AppErrorMapper.mapStatusCode(400).userMessage
        )
        assertEquals(
            "Please sign in again.",
            AppErrorMapper.mapStatusCode(401).userMessage
        )
        assertEquals(
            "Requested content was not found.",
            AppErrorMapper.mapStatusCode(404).userMessage
        )
        assertEquals(
            "Too many attempts. Please try again later.",
            AppErrorMapper.mapStatusCode(429).userMessage
        )
        assertEquals(
            "Server is having trouble. Please try again later.",
            AppErrorMapper.mapStatusCode(500).userMessage
        )
    }

    @Test
    fun `maps network exceptions to connectivity message`() {
        val error = AppErrorMapper.toAppError(UnknownHostException("offline"))

        assertEquals(AppErrorType.NetworkUnavailable, error.type)
        assertEquals(AppErrorMessages.NETWORK_UNAVAILABLE, error.userMessage)
    }

    @Test
    fun `unknown errors do not expose raw message`() {
        val error = AppErrorMapper.toAppError(IllegalStateException("URL: https://example.com"))

        assertEquals("Something went wrong. Please try again.", error.userMessage)
    }
}
