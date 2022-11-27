package com.example.finalproject.api

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface BookApi {
    @GET("/books/v1/volumes?maxResults=40")
    suspend fun getBooksBySearchTerm(@Query("q") searchTerm: String) : BookResponse

    data class BookResponse(val items: List<BookItem>)

    companion object {
        var url = HttpUrl.Builder()
            .scheme("https")
            .host("www.googleapis.com")
            .build()

        fun create(): BookApi = create(url)
        private fun create(httpUrl: HttpUrl): BookApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookApi::class.java)
        }
    }
}