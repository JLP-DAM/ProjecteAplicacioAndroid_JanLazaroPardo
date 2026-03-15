package com.gilded.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gilded.models.Category
import com.gilded.services.GildedAPI
import kotlinx.coroutines.launch

class CategoriesViewModel: ViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _ownerId = MutableLiveData<Long>()

    fun setOwnerId(ownerId: Long) {
        _ownerId.value = ownerId
    }

    fun setCategories(receipts: List<Category>) {
        _categories.value = receipts
    }

    fun getCategories(): List<Category> {
        return _categories.value ?: listOf()
    }

    fun addCategory(category: Category) {
        if (getCategory(category.name) != null) {return}

        viewModelScope.launch {
            val completeCategory = GildedAPI.API().postCategory(category)

            val categoriesClone = _categories.value?.toMutableList() ?: mutableListOf()
            categoriesClone.add(completeCategory)
            _categories.value = categoriesClone
        }

    }

    fun removeCategory(index: Int) {
        val categoriesClone = _categories.value?.toMutableList() ?: mutableListOf()
        categoriesClone.removeAt(index)
        _categories.value = categoriesClone
    }

    fun getCategory(name: String): Category? {
        var foundCategory: Category? = null

        for (category in _categories.value) {
            if (category.name != name) {continue}

            foundCategory = category
            break
        }

        return foundCategory
    }

    fun loadCategories() {
        viewModelScope.launch {
            val result = GildedAPI.API().getCategories(_ownerId.value!!)

            _categories.value = result.body() ?: emptyList()
        }
    }
}