package com.example.finalproject.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.api.BookApi
import com.example.finalproject.api.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is search Fragment"
    }
    val text: LiveData<String> = _text

    private val api = BookApi.create()
    private val repository = Repository(api)

    init {
        netRefresh()
    }

    fun netRefresh() {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            val bookInfoList = repository.fetchBooks("quilting")
            Log.d("XXX", bookInfoList.toString())
        }
    }
}