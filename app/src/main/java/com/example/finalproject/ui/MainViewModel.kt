package com.example.finalproject.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.FirestoreAuthLiveData
import com.example.finalproject.ViewModelDBHelper
import com.example.finalproject.api.BookApi
import com.example.finalproject.api.BookInfo
import com.example.finalproject.api.Repository
import com.example.finalproject.model.BookReview
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val api = BookApi.create()
    private val repository = Repository(api)
    private val dbHelp = ViewModelDBHelper()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()

    private val bookListings = MutableLiveData<List<BookInfo>>()
    private var bookReviewList = MutableLiveData<List<BookReview>>()
    private var userBookReviewList = MutableLiveData<List<BookReview>>()
    private var loginStatus = MediatorLiveData<Boolean>().apply {
        addSource(firebaseAuthLiveData) {
            value = (it != null)
        }
    }

    // BOOK LISTINGS
    fun netBooks(searchTerm: String) {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            bookListings.postValue(repository.fetchBooksBySearchTerm(searchTerm))
        }
    }

    fun observeBookListings(): LiveData<List<BookInfo>> {
        return bookListings
    }

    // BOOK PAGE
    fun openBookPageByVolumeID(volumeID: String, context: Context) {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            val bookInfo = repository.fetchBookByVolumeID(volumeID)
            if (bookInfo != null) {
                openBookPage(context, bookInfo)
            }
        }
    }

    // BOOK REVIEWS
    fun createBookReview(
        bookReviewText: String,
        bookReviewRating: Float,
        bookReviewVolumeID: String,
        bookReviewTitle: String,
        bookReviewAuthors: List<String>,
    ) {
        val currUser = firebaseAuthLiveData.getCurrentUser()
        if (currUser != null) {
            val bookReview = BookReview(
                text = bookReviewText,
                rating = bookReviewRating,
                email = currUser.email!!,
                volumeID = bookReviewVolumeID,
                title = bookReviewTitle,
                authors = bookReviewAuthors,
            )
            dbHelp.createBookReview(bookReview, bookReviewList, userBookReviewList)
        }
    }

    fun observeBookReviews(): LiveData<List<BookReview>> {
        return bookReviewList
    }

    fun observeUserBookReviews(): LiveData<List<BookReview>> {
        return userBookReviewList
    }

    fun getBookReview(position: Int) : BookReview {
        val bookReview = bookReviewList.value?.get(position)
        return bookReview!!
    }

    fun getUserBookReview(position: Int) : BookReview {
        val userBookReview = userBookReviewList.value?.get(position)
        return userBookReview!!
    }

    fun fetchInitialBookReviewsByVolumeID(volumeID: String) {
        dbHelp.fetchInitialBookReviewsByVolumeID(bookReviewList, volumeID)
    }

    fun fetchInitialBookReviewsByEmail(email: String) {
        dbHelp.fetchInitialBookReviewsByEmail(userBookReviewList, email)
    }

    // AUTHENTICATION
    fun observeLoginStatus(): LiveData<Boolean> {
        return loginStatus
    }

    fun getLoginStatus(): Boolean {
        return firebaseAuthLiveData.getCurrentUser() != null
    }

    fun getDisplayName(): String {
        val currUser = firebaseAuthLiveData.getCurrentUser()
        return if (currUser != null) {
            if (currUser.displayName != null) {
                currUser.displayName!!
            } else {
                currUser.email!!
            }
        } else {
            "Anonymous User"
        }
    }

    fun getEmail(): String {
        val currUser = firebaseAuthLiveData.getCurrentUser()
        return if (currUser != null) {
            currUser.email!!
        } else {
            "Anonymous User"
        }
    }

    fun updateUser() {
        firebaseAuthLiveData.updateUser()
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    // MISC
    companion object {
        val bookReviewKey = "bookReview"
        val userBookReviewKey = "userBookReview"

        fun openBookPage(context: Context, bookInfo: BookInfo) {
            val launchBookPageIntent = Intent(context, BookPage::class.java)
            launchBookPageIntent.apply {
                putExtra(BookPage.volumeIDKey, bookInfo.volumeID)
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