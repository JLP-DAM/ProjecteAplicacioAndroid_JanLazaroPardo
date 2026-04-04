package com.gilded.viewmodels

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gilded.models.Receipt
import androidx.lifecycle.viewModelScope
import com.gilded.services.GildedAPI
import com.gilded.services.UsageData
import kotlinx.coroutines.launch
import kotlin.getValue

class ReceiptsViewModel: ViewModel() {

    private val _receipts = MutableLiveData<List<Receipt>>()
    val receipts: LiveData<List<Receipt>> = _receipts

    private val _ownerId = MutableLiveData<Long>()

    fun setOwnerId(ownerId: Long) {
        _ownerId.value = ownerId
    }

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
            val completeReceipt = GildedAPI.API().postReceipt(receipt)

            val receiptsClone = _receipts.value?.toMutableList() ?: mutableListOf()
            receiptsClone.add(completeReceipt)
            _receipts.value = receiptsClone

            UsageData.receiptCreations = UsageData.receiptCreations + 1
        }
    }

    fun removeReceipt(index: Int) {
        val receiptsClone = _receipts.value?.toMutableList() ?: mutableListOf()
        val receipt = receiptsClone.removeAt(index)
        _receipts.value = receiptsClone

        viewModelScope.launch {
            if (receipt == null) {return@launch}

            GildedAPI.API().deleteReceipt(receipt.id!!)

            UsageData.receiptDeletions = UsageData.receiptDeletions + 1
        }
    }

    fun updateReceipt(receipt: Receipt) {
        val foundReceipt = getReceipt(getReceiptIndex(receipt)) ?: return

        foundReceipt.recipient = receipt.recipient
        foundReceipt.amount = receipt.amount
        foundReceipt.timestamp = receipt.timestamp
        foundReceipt.category = receipt.category
        foundReceipt.ownerId = receipt.ownerId

        viewModelScope.launch {
            GildedAPI.API().updateReceipt(receipt)
        }
    }

    fun loadReceipts() {
        viewModelScope.launch {
            val result = GildedAPI.API().getReceipts(_ownerId.value!!)

            _receipts.value = result.body() ?: emptyList()
        }
    }
}