package com.gilded.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.gilded.R
import com.gilded.services.GildedAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val loginFragmentView = inflater.inflate(R.layout.fragment_login, container, false)

        val emailEditText: EditText = loginFragmentView.findViewById(R.id.email)
        val passwordEditText: EditText = loginFragmentView.findViewById(R.id.password)
        val loginButton: Button = loginFragmentView.findViewById(R.id.login)
        val signInButton: Button = loginFragmentView.findViewById(R.id.signin)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            lifecycleScope.launch(Dispatchers.IO) {
                val user = GildedAPI.API().getUser(email, password).body()

                if (user == null) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(context, "No existeix cap usuari amb aquest correu i contrasenya", Toast.LENGTH_SHORT).show()
                    }

                    return@launch
                }

                lifecycleScope.launch(Dispatchers.Main) {
                    val homeFragment = HomeFragment()

                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragmentContainerView, homeFragment)
                        ?.commit()
                }
            }
        }

        signInButton.setOnClickListener {
            val signInFragment = SignInFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, signInFragment)
                ?.commit()
        }

        return loginFragmentView
    }
}