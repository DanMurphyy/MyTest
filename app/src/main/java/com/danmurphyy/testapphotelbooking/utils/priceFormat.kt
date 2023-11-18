package com.danmurphyy.testapphotelbooking.utils

fun priceFormat(price: Int?): String {
    val formattedPrice = price.toString()
    val builder = StringBuilder()
    var count = 0
    for (i in formattedPrice.length - 1 downTo 0) {
        builder.append(formattedPrice[i])
        count++
        if (count % 3 == 0 && i != 0) {
            builder.append(' ')
        }
    }

    return builder.reverse().toString()
}