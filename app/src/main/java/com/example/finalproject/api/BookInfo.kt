package com.example.finalproject.api

import com.google.gson.annotations.SerializedName

data class BookInfo(
    var volumeID: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("subtitle")
    val subtitle: String,
    @SerializedName("authors")
    val authors: List<String>,
    @SerializedName("publisher")
    val publisher: String,
    @SerializedName("publishedDate")
    val publishedDate: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("industryIdentifiers")
    val industryIdentifiers: List<ISBNNumber>,
    @SerializedName("pageCount")
    val pageCount: Int,
    @SerializedName("categories")
    val categories: List<String>,
    @SerializedName("imageLinks")
    val imageLinks: ImageLink
)
