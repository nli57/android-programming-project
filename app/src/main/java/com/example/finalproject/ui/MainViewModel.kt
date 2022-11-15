package com.example.finalproject.ui

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

    private val bookListings = MutableLiveData<List<BookInfo>>().apply {
        value = listOf()
    }

    init {
        netBooks()
    }

    fun netBooks() {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            bookListings.postValue(repository.fetchBooks("quilting"))
        }
    }

    fun observeBookListings(): LiveData<List<BookInfo>> {
        return bookListings
    }
}