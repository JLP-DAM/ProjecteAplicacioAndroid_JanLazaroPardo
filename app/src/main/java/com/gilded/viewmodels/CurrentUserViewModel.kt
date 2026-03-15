package com.gilded.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gilded.models.User

class CurrentUserViewModel: ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun setUser(user: User) {
        _user.value = user
    }
}