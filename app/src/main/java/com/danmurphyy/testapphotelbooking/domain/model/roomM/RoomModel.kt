package com.danmurphyy.testapphotelbooking.domain.model.roomM

data class RoomModel(
    val id: Int,
    val name: String,
    val price: Int,
    val price_per: String,
    val peculiarities: List<String>,
    val image_urls: List<String>
)

