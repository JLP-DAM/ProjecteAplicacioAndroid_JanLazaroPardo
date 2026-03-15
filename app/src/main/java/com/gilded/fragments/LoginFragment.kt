package com.gilded.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.gilded.R
import com.gilded.models.User
import com.gilded.services.GildedAPI
import com.gilded.viewmodels.CategoriesViewModel
import com.gilded.viewmodels.CurrentUserViewModel
import com.gilded.viewmodels.ReceiptsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.getValue

class LoginFragment : Fragment() {

    private val currentUserViewModel: CurrentUserViewModel by activityViewModels()
    private val receiptsViewModel: ReceiptsViewModel by activityViewModels()
    private val categoriesViewModel: CategoriesViewModel by activityViewModels()

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
                var user: User? = null

                try {
                    user = GildedAPI.API().getUser(email, password).body()
                } catch(exception: Exception) {}

                if (user == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "No existeix cap usuari amb aquest correu i contrasenya", Toast.LENGTH_SHORT).show()
                    }

                    return@launch
                }

                withContext(Dispatchers.Main) {
                    currentUserViewModel.setUser(user)

                    receiptsViewModel.setOwnerId(user.id!!)
                    categoriesViewModel.setOwnerId(user.id!!)

                    receiptsViewModel.loadReceipts()
                    categoriesViewModel.loadCategories()

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