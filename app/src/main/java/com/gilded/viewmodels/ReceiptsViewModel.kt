package com.gilded.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gilded.models.Receipt
import androidx.lifecycle.viewModelScope
import com.gilded.services.ReceiptAPI
import kotlinx.coroutines.launch

class ReceiptsViewModel: ViewModel() {
    private val _receipts = MutableLiveData<List<Receipt>>()
    val receipts: LiveData<List<Receipt>> = _receipts

    fun setReceipts(receipts: List<Receipt>) {
        _receipts.value = receipts
    }

    fun getReceipts(): List<Receipt> {
        return _receipts.value ?: listOf()
    }

    fun getReceipt(index: Int?): Receipt? {
        if (index == null) {return null}

        if (index <= -1 || index >= _receipts.value!!.size) {return null}

        return _receipts.value!![index]
    }

    fun getReceiptIndex(receipt: Receipt?): Int {
        if (receipt == null) {return -1}

        return _receipts.value!!.indexOf(receipt)
    }

    fun addReceipt(receipt: Receipt) {
        viewModelScope.launch {
            val completeReceipt = ReceiptAPI.API().postReceipt(receipt)

            val receiptsClone = _receipts.value?.toMutableList() ?: mutableListOf()
            receiptsClone.add(completeReceipt)
            _receipts.value = receiptsClone
        }
    }

    fun removeReceipt(index: Int) {
        val receiptsClone = _receipts.value?.toMutableList() ?: mutableListOf()
        val receipt = receiptsClone.removeAt(index)
        _receipts.value = receiptsClone

        viewModelScope.launch {
            if (receipt == null) {return@launch}

            ReceiptAPI.API().deleteReceipt(receipt.id!!)
        }
    }

    fun updateReceipt(receipt: Receipt) {
        val foundReceipt = getReceipt(getReceiptIndex(receipt)) ?: return

        foundReceipt.recipient = receipt.recipient
        foundReceipt.amount = receipt.amount
        foundReceipt.timestamp = receipt.timestamp
        foundReceipt.category = receipt.category

        viewModelScope.launch {
            ReceiptAPI.API().updateReceipt(receipt)
        }
    }

    fun loadReceipts() {
        viewModelScope.launch {
            val result = ReceiptAPI.API().getReceipts()

            _receipts.value = result.body() ?: emptyList()
        }
    }
}