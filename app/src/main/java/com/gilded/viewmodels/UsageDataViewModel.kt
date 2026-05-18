package com.gilded.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gilded.models.Receipt
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class UsageDataViewModel: ViewModel() {
    private val _isLoaded = MutableLiveData<Boolean>(false)
    val isLoaded: LiveData<Boolean> = _isLoaded

    private val _receiptCreations = MutableLiveData<Int>(0)
    val receiptCreations: LiveData<Int> = _receiptCreations

    private val _receiptDeletions = MutableLiveData<Int>(0)
    val receiptDeletions: LiveData<Int> = _receiptDeletions

    private val _usageTime = MutableLiveData<Double>(0.0)
    val usageTime: LiveData<Double> = _usageTime

    private val _userId = MutableLiveData<Long>()
    val userId: LiveData<Long> = _userId

    private val firestoreDatabase: FirebaseFirestore by lazy { Firebase.firestore }

    fun setUserId(userId: Long) {
        _userId.value = userId
    }

    fun incrementCreations() {
        _receiptCreations.value = (_receiptCreations.value ?: 0) + 1
    }

    fun incrementDeletions() {
        _receiptDeletions.value = (_receiptDeletions.value ?: 0) + 1
    }

    fun incrementUsageTime(addedUsageTime: Double) {
        _usageTime.value = (_usageTime.value ?: 0.0) + addedUsageTime
    }

    fun saveToFirebase() {
        if (_userId.value == null) {return}

        val usageData = hashMapOf<String, Number>(
            "receiptCreations" to receiptCreations.value,
            "receiptDeletions" to receiptDeletions.value,
            "usageTime" to usageTime.value,
        )

        firestoreDatabase.collection("usageStats").document(userId.value!!.toString())
            .set(usageData)
    }

    fun getFromFirebase() {
        if (_userId.value == null) {return}
        if (_isLoaded.value == true) {return}
        _isLoaded.value = true

        firestoreDatabase.collection("usageStats").document(userId.value!!.toString())
            .get()
            .addOnSuccessListener { doc ->
                if (doc == null || !doc.exists()) {
                    return@addOnSuccessListener
                }

                _receiptCreations.value = doc.getLong("receiptCreations")?.toInt() ?: 0
                _receiptDeletions.value = doc.getLong("receiptDeletions")?.toInt() ?: 0
                _usageTime.value = doc.getDouble("usageTime") ?: 0.0
            }
            .addOnFailureListener {}
    }
}