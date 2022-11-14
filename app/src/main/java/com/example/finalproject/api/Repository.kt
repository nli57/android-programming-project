package com.example.finalproject.api

class Repository(private val api: BookApi) {
    suspend fun fetchBooks(searchTerm: String): List<BookInfo> {
        val bookInfoList = mutableListOf<BookInfo>()
        val items = api.getBooks(searchTerm).items

        for (item in items) {
            bookInfoList.add(item.volumeInfo)
        }
        return bookInfoList
    }
}