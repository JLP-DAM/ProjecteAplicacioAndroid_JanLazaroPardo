package com.gilded

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReceiptsViewModel: ViewModel() {
    private val _receipts = MutableLiveData<List<Receipt>>()
    val receipts: LiveData<List<Receipt>> = _receipts

    fun setReceipts(receipts: List<Receipt>) {
        _receipts.value = receipts
    }

    fun getReceipts(): List<Receipt> {
        return _receipts.value ?: listOf()
    }

    fun addReceipt(receipt: Receipt) {
        val receiptsClone = _receipts.value?.toMutableList() ?: mutableListOf()
        receiptsClone.add(receipt)
        _receipts.value = receiptsClone
    }

    fun removeReceipt(index: Int) {
        val receiptsClone = _receipts.value?.toMutableList() ?: mutableListOf()
        receiptsClone.removeAt(index)
        _receipts.value = receiptsClone
    }
}