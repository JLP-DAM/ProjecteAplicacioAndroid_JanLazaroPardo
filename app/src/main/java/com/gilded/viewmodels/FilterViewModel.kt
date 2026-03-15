package com.gilded.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FilterViewModel: ViewModel() {
    // wanted to do a multi-select dropdown of some kind for categories, however, can't figure out for now and there is no time
    // maybe ill do it at a later date
    private val _incomeVisible = MutableLiveData<Boolean>(true)
    val incomeVisible: LiveData<Boolean> = _incomeVisible

    private val _expensesVisible = MutableLiveData<Boolean>(true)
    val expensesVisible: LiveData<Boolean> = _expensesVisible

    private val _filteredCategory = MutableLiveData<String?>(null)
    val filteredCategory: LiveData<String?> = _filteredCategory

    fun setIncomeVisible(incomeVisible: Boolean) {
        _incomeVisible.value = incomeVisible
    }

    fun setExpensesVisible(expensesVisible: Boolean) {
        _expensesVisible.value = expensesVisible
    }

    fun setFilteredCategory(category: String?) {
        _filteredCategory.value = category
    }

    fun reset() {
        _incomeVisible.value = true
        _expensesVisible.value = true

        _filteredCategory.value = null
    }
}