package com.gilded

import com.gilded.utils.SignInValidator
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun invalidName() {
        assertFalse(
            SignInValidator.isEmailValid("")
        )
    }

    @Test
    fun validEmail() {
        assertTrue(
            SignInValidator.isEmailValid("test@gmail.com")
        )
    }

    @Test
    fun invalidEmail() {
        assertFalse(
            SignInValidator.isEmailValid("testgmail.com")
        )
    }

    @Test
    fun validPassword() {
        assertTrue(
            SignInValidator.isPasswordValid("rfewsfaefBHA1251") == null
        )
    }

    @Test
    fun invalidPasswordTooShort() {
        assertFalse(
            SignInValidator.isPasswordValid("1") == null
        )
    }

    @Test
    fun invalidPasswordTooNoNumber() {
        assertFalse(
            SignInValidator.isPasswordValid("AbAGVFSDBGVAsf") == null
        )
    }
}