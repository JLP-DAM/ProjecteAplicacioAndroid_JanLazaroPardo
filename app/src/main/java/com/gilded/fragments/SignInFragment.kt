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
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.gilded.R
import com.gilded.models.User
import com.gilded.services.GildedAPI
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

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(context, "Correu electronic invalid", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            if (password.length < 8) {
                Toast.makeText(context, "Contrasenya massa curta", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            if ("[A-Z]".toRegex().find(password) == null) {
                Toast.makeText(context, "Contrasenya no te cap majúscula", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            if ("[a-z]".toRegex().find(password) == null) {
                Toast.makeText(context, "Contrasenya no te cap minúscula", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            if ("[1-9]".toRegex().find(password) == null) {
                Toast.makeText(context, "Contrasenya no te cap nombre", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

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