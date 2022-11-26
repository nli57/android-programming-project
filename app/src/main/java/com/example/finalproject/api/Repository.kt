package com.example.finalproject.api

class Repository(private val api: BookApi) {
    suspend fun fetchBooks(searchTerm: String): List<BookInfo> {
        val bookInfoList = mutableListOf<BookInfo>()
        val bookResponse = api.getBooksBySearchTerm(searchTerm)
        if (bookResponse != null) {
            val items = bookResponse.items
            if (items != null) {
                for (item in items) {
                    bookInfoList.add(item.volumeInfo)
                }
            }
        }
        return bookInfoList
    }
}