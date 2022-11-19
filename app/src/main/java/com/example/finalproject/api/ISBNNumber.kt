package com.example.finalproject.api

import com.google.gson.annotations.SerializedName

data class ISBNNumber(
    @SerializedName("type")
    val type: String,
    @SerializedName("identifier")
    val identifier: String
)
