package com.gilded.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {
    private val _voiceNavigation = MutableLiveData<Boolean>(false)
    val voiceNavigation: LiveData<Boolean> = _voiceNavigation

    fun setVoiceNavigation(newVoiceNavigation: Boolean) {
        _voiceNavigation.value = newVoiceNavigation
    }
}