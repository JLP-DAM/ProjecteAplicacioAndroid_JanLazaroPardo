package com.gilded.utils

import android.widget.Toast

class SignInValidator {
    fun isUsernameValid(username: String): Boolean {
        return !username.isEmpty()
    }

    fun isEmailValid(email: String): Boolean {
        val emailRegex =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"

        return email.matches(emailRegex.toRegex())
    }

    fun isPasswordValid(password: String): String? {
        if (password.length < 8) {
            return "Contrasenya massa curta"
        }

        if ("[A-Z]".toRegex().find(password) == null) {
            return "Contrasenya no te cap majúscula"
        }

        if ("[a-z]".toRegex().find(password) == null) {
            return "Contrasenya no te cap minúscula"
        }

        if ("[1-9]".toRegex().find(password) == null) {
            return "Contrasenya no te cap nombre"
        }

        return null
    }
}