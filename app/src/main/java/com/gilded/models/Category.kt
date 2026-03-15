package com.gilded.models

data class Category(
    var id: Long?,
    val name: String,
    val color: Int,

    var ownerId: Long,
)
