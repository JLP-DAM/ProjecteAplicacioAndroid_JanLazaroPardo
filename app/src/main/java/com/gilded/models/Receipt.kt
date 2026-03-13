package com.gilded.models

data class Receipt (
    var id: Long?,
    var recipient: String,
    var amount: Double,
    var timestamp: Long,
    var category: String
)