package com.gilded.models

data class Receipt (
    var recipient: String,
    var amount: Double,
    var timestamp: Long,
    var category: String 
)