package com.gilded

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CurrentBalanceViewModel: ViewModel() {
    private val _balance = MutableLiveData<Double>(0.0)
    val balance: LiveData<Double> = _balance

    fun setBalance(newBalance: Double) {
        _balance.value = newBalance
    }

    fun changeBalance(balanceChange: Double) {
        _balance.value = _balance.value?.plus(balanceChange)
    }
}