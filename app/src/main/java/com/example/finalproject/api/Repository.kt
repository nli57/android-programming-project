package com.example.finalproject.api

class Repository(private val api: BookApi) {
    suspend fun fetchBooksBySearchTerm(searchTerm: String): List<BookInfo> {
        val bookInfoList = mutableListOf<BookInfo>()
        val bookResponse = api.getBooksBySearchTerm(searchTerm)
        if (bookResponse != null) {
            val items = bookResponse.items
            if (items != null) {
                for (item in items) {
                    item.volumeInfo.volumeID = item.id
                    bookInfoList.add(item.volumeInfo)
                }
            }
        }
        return bookInfoList
    }

    suspend fun fetchBookByVolumeID(volumeID: String): BookInfo? {
        val bookItem = api.getBookByVolumeID(volumeID)
        if (bookItem != null) {
            bookItem.volumeInfo.volumeID = bookItem.id
            return bookItem.volumeInfo
        }
        return null
    }
}