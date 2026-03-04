package com.gilded

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CategoriesViewModel: ViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    fun setCategories(receipts: List<Category>) {
        _categories.value = receipts
    }

    fun getCategories(): List<Category> {
        return _categories.value ?: listOf()
    }

    fun addCategory(receipt: Category) {
        val categoriesClone = _categories.value?.toMutableList() ?: mutableListOf()
        categoriesClone.add(receipt)
        _categories.value = categoriesClone
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
        }

        return foundCategory
    }
}