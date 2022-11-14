package com.example.finalproject.api

import com.google.gson.annotations.SerializedName

data class ImageLink(
    @SerializedName("smallThumbnail")
    val smallThumbnail: String,
    @SerializedName("thumbnail")
    val thumbnail: String
)
