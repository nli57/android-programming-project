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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val api = BookApi.create()
    private val repository = Repository(api)
    private val bookListings = MutableLiveData<List<BookInfo>>()
    private var displayName = MutableLiveData("Uninitialized")
    private var email = MutableLiveData("Uninitialized")
    private var uid = MutableLiveData("Uninitialized")
    private var isLoggedIn = MutableLiveData(false)

    fun netBooks(searchTerm: String) {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            bookListings.postValue(repository.fetchBooks(searchTerm))
        }
    }

    fun observeBookListings(): LiveData<List<BookInfo>> {
        return bookListings
    }

    fun observeDisplayName(): LiveData<String> {
        return displayName
    }

    fun observeLoginStatus(): LiveData<Boolean> {
        return isLoggedIn
    }

    fun getLoginStatus(): Boolean {
        return isLoggedIn.value!!
    }

    fun updateUser() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val profile = user.providerData[0]
            val name = profile.displayName
            if (name != null) {
                displayName.postValue(name)
            }
            email.postValue(profile.email)
            uid.postValue(profile.uid)
            isLoggedIn.postValue(true)
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        userLogout()
    }

    private fun userLogout() {
        displayName.postValue("Uninitialized")
        email.postValue("Uninitialized")
        uid.postValue("Uninitialized")
        isLoggedIn.postValue(false)
    }

    companion object {
        fun openBookPage(context: Context, bookInfo: BookInfo) {
            val launchBookPageIntent = Intent(context, BookPage::class.java)
            launchBookPageIntent.apply {
                putExtra(BookPage.titleKey, bookInfo.title)
                putExtra(BookPage.subtitleKey, bookInfo.subtitle)
                putExtra(BookPage.authorsKey, bookInfo.authors.toTypedArray())
                putExtra(BookPage.descriptionKey, bookInfo.description)
                putExtra(BookPage.imageLinkKey, bookInfo.imageLinks.thumbnail)
                putExtra(BookPage.isbn10Key, findISBN(bookInfo, "ISBN_10"))
                putExtra(BookPage.isbn13Key, findISBN(bookInfo, "ISBN_13"))
                putExtra(BookPage.publisherKey, bookInfo.publisher)
                putExtra(BookPage.publishedDateKey, bookInfo.publishedDate)
                putExtra(BookPage.pageCountKey, bookInfo.pageCount)
                putExtra(BookPage.categoriesKey, bookInfo.categories.toTypedArray())
            }
            context.startActivity(launchBookPageIntent)
        }

        private fun findISBN(bookInfo: BookInfo, type: String): String {
            val industryIdentifiers = bookInfo.industryIdentifiers
            for (isbnNum in industryIdentifiers) {
                if (isbnNum.type == type) {
                    return isbnNum.identifier
                }
            }
            return ""
        }
    }
}