package com.example.finalproject.api

import com.google.gson.annotations.SerializedName

data class BookItem(
    @SerializedName("volumeInfo")
    val volumeInfo: BookInfo
)
