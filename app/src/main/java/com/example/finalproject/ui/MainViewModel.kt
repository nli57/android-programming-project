package com.example.finalproject.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.api.BookApi
import com.example.finalproject.api.BookInfo
import com.example.finalproject.api.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val api = BookApi.create()
    private val repository = Repository(api)

    private val bookListings = MutableLiveData<List<BookInfo>>()

    init {
        netBooks("quilting")
    }

    fun netBooks(searchTerm: String) {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            bookListings.postValue(repository.fetchBooks(searchTerm))
        }
    }

    fun observeBookListings(): LiveData<List<BookInfo>> {
        return bookListings
    }

    companion object {
        fun openBookPage(context: Context, bookInfo: BookInfo) {
            val launchBookPageIntent = Intent(context, BookPage::class.java)
            launchBookPageIntent.apply {
                putExtra(BookPage.titleKey, bookInfo.title)
                putExtra(BookPage.authorsKey, bookInfo.authors.toTypedArray())
                putExtra(BookPage.descriptionKey, bookInfo.description)
                putExtra(BookPage.imageLinkKey, bookInfo.imageLinks.thumbnail)
            }
            context.startActivity(launchBookPageIntent)
        }
    }
}