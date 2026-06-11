package com.example.curate.presentation.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthFormValidatorTest {
    @Test
    fun `password shorter than six characters fails`() {
        assertEquals(
            "Password must be at least 6 characters.",
            AuthFormValidator.validatePassword("a1@")
        )
    }

    @Test
    fun `password without number fails`() {
        assertEquals(
            "Password must include a number.",
            AuthFormValidator.validatePassword("abcdef@")
        )
    }

    @Test
    fun `password without special character fails`() {
        assertEquals(
            "Password must include a special character.",
            AuthFormValidator.validatePassword("abcdef1")
        )
    }

    @Test
    fun `password with number and special character passes`() {
        assertNull(AuthFormValidator.validatePassword("abc1@2"))
    }
}
