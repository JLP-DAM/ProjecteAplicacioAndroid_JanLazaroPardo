package com.gilded.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.gilded.R
import com.gilded.models.User
import com.gilded.services.GildedAPI
import com.gilded.utils.SignInValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class SignInFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val signInFragment = inflater.inflate(R.layout.fragment_signin, container, false)

        val usernameEditText: EditText = signInFragment.findViewById(R.id.username)
        val emailEditText: EditText = signInFragment.findViewById(R.id.email)
        val passwordEditText: EditText = signInFragment.findViewById(R.id.password)
        val signInButton: Button = signInFragment.findViewById(R.id.signin)
        val goBackImageButton: ImageButton = signInFragment.findViewById(R.id.goBack)

        val errorTextView: TextView = signInFragment.findViewById(R.id.error)

        fun goBack() {
            val loginFragment = LoginFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, loginFragment)
                ?.commit()
        }

        signInButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (!SignInValidator.isUsernameValid(username)) {
                Toast.makeText(context, "Nom d'usuari invalid", Toast.LENGTH_SHORT).show()
                errorTextView.setText("Nom d'usuari invalid")

                return@setOnClickListener
            }

            if (!SignInValidator.isEmailValid(email)) {
                Toast.makeText(context, "Correu electronic invalid", Toast.LENGTH_SHORT).show()
                errorTextView.setText("Correu electronic invalid")

                return@setOnClickListener
            }

            val passwordValidError = SignInValidator.isPasswordValid(password)

            if (passwordValidError != null) {
                Toast.makeText(context, passwordValidError, Toast.LENGTH_SHORT).show()
                errorTextView.setText(passwordValidError)

                return@setOnClickListener
            }

            errorTextView.setText("Registre correcte")

            lifecycleScope.launch(Dispatchers.IO) {
                var user: User? = null
                try {
                    user = GildedAPI.API().getUser(email, password).body()
                } catch(exception: Exception) {}

                if (user != null) {

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Hi han hagut problemes a l'hora de crear un usuari (correu ja utilitzat?)", Toast.LENGTH_SHORT).show()
                    }

                    return@launch
                }

                var postedUser: User? = null

                try {
                    postedUser = GildedAPI.API().postUser(User(null, username, email, password))
                } catch(exception: Exception) {}

                if (postedUser == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Hi han hagut problemes a l'hora de crear un usuari", Toast.LENGTH_SHORT).show()
                    }

                    return@launch
                }

                withContext(Dispatchers.Main) {
                    goBack()
                }
            }


        }

        goBackImageButton.setOnClickListener {
            goBack()
        }

        return signInFragment
    }
}