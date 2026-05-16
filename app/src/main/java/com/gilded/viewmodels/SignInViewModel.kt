package com.gilded.viewmodels

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gilded.utils.SignInValidator

class SignInViewModel: ViewModel() {
    val errorMessage = MutableLiveData<String?>()

    fun signInUser(username: String, email: String, password: String) {
        errorMessage.value = null

        val signInValidator = SignInValidator()

        if (!signInValidator.isUsernameValid(username)) {
            errorMessage.value = "Nom d'usuari invalid"

            return
        }

        if (!signInValidator.isEmailValid(email)) {
            errorMessage.value = "Correu electronic invalid"

            return
        }

        val passwordValidError = signInValidator.isPasswordValid(password)

        if (passwordValidError != null) {
            errorMessage.value = passwordValidError

            return
        }
    }
}