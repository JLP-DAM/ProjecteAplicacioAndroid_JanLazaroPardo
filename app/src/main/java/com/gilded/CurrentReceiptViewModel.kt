package com.gilded

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CurrentReceiptViewModel: ViewModel() {
    private val _receipt = MutableLiveData<Receipt>()
    val receipt: LiveData<Receipt> = _receipt

    fun setReceipt(receipt: Receipt) {
        _receipt.value = receipt
    }
}