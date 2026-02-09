package com.gilded

data class Receipt (
    var recipient: String,
    var amount: Double,
    var timestamp: Long,
    var section: String
)