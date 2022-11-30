package com.example.finalproject.ui

import android.content.Context
import android.content.Intent
import android.view.View
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
import com.example.finalproject.model.ReadingListBook
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val api = BookApi.create()
    private val repository = Repository(api)
    private val dbHelp = ViewModelDBHelper()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()

    private var bookListings = MutableLiveData<List<BookInfo>>()
    private var bookReviewList = MutableLiveData<List<BookReview>>()
    private var userBookReviewList = MutableLiveData<List<BookReview>>()
    private var readingListBookList = MutableLiveData<List<ReadingListBook>>().apply {
        value = listOf()
    }
    private var readingListBookListings = MediatorLiveData<List<BookInfo>>().apply {
        addSource(readingListBookList) {
            getReadingListBookListings(readingListBookList.value!!)
        }
    }
    private var currReadingListName = MutableLiveData<String>().apply {
        value = wantToReadKey
    }
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

    // FETCH BOOK BY VOLUME ID
    fun openBookPageByVolumeID(volumeID: String, context: Context) {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            val bookInfo = repository.fetchBookByVolumeID(volumeID)
            if (bookInfo != null) {
                openBookPage(context, bookInfo)
            }
        }
    }

    private fun getReadingListBookListings(readingListBookList: List<ReadingListBook>) {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            val bookInfoList = mutableListOf<BookInfo>()
            for (readingListBook in readingListBookList) {
                val volumeID = readingListBook.volumeID
                val bookInfo = repository.fetchBookByVolumeID(volumeID)
                if (bookInfo != null) {
                    bookInfoList.add(bookInfo)
                }
            }
            readingListBookListings.postValue(bookInfoList)
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

    fun updateBookReview(bookReview: BookReview) {
        dbHelp.updateBookReview(bookReview, bookReviewList, userBookReviewList)
    }

    fun deleteBookReview(bookReview: BookReview) {
        dbHelp.deleteBookReview(bookReview, bookReviewList, userBookReviewList)
    }

    fun observeBookReviews(): LiveData<List<BookReview>> {
        return bookReviewList
    }

    fun observeUserBookReviews(): LiveData<List<BookReview>> {
        return userBookReviewList
    }

    fun getBookReview(position: Int) : BookReview {
        return bookReviewList.value?.get(position)!!
    }

    fun getUserBookReview(position: Int) : BookReview {
        return userBookReviewList.value?.get(position)!!
    }

    fun fetchInitialBookReviewsByVolumeID(volumeID: String) {
        dbHelp.fetchInitialBookReviewsByVolumeID(bookReviewList, volumeID)
    }

    fun fetchInitialBookReviewsByEmail(email: String) {
        dbHelp.fetchInitialBookReviewsByEmail(userBookReviewList, email)
    }

    // READING LISTS
    fun addBookToReadingList(bookVolumeID: String, readingListName: String) {
        val currUser = firebaseAuthLiveData.getCurrentUser()
        if (currUser != null) {
            val readingListBook = ReadingListBook(
                volumeID = bookVolumeID,
                listName = readingListName,
                uid = currUser.uid
            )
            dbHelp.addBookToReadingList(
                readingListBook,
                readingListBookList,
                currReadingListName.value!!
            )
        }
    }

    fun updateReadingListBook(
        volumeID: String,
        listName: String,
        uid: String,
        view: View,
        updateReadingListBookSuccess: (
            volumeID: String, listName: String, readingListBook: ReadingListBook?, view: View
        ) -> Unit,
        updateReadingListBookFailure: (view: View) -> Unit,
        readingListBook: ReadingListBook? = null
    ) {
        dbHelp.updateReadingListBook(
            volumeID,
            listName,
            uid,
            view,
            updateReadingListBookSuccess,
            updateReadingListBookFailure,
            readingListBook
        )
    }

    fun removeBookFromReadingList(readingListBook: ReadingListBook) {
        dbHelp.removeBookFromReadingList(
            readingListBook,
            readingListBookList,
            currReadingListName.value!!
        )
    }

    fun getReadingListBook(position: Int) : ReadingListBook {
        return readingListBookList.value?.get(position)!!
    }

    fun observeReadingListBookListings(): LiveData<List<BookInfo>> {
        return readingListBookListings
    }

    fun fetchInitialReadingListBooksByListNameAndUID(uid: String) {
        dbHelp.fetchInitialReadingListBooksByListNameAndUID(
            readingListBookList,
            currReadingListName.value!!,
            uid
        )
    }

    fun setCurrReadingListName(listName: String) {
        currReadingListName.value = listName
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

    fun getUID(): String {
        return firebaseAuthLiveData.getCurrentUser()?.uid ?: "N/A"
    }

    fun updateUser() {
        firebaseAuthLiveData.updateUser()
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    // MISC
    companion object {
        const val bookReviewKey = "bookReview"
        const val userBookReviewKey = "userBookReview"
        const val wantToReadKey = "Want To Read"
        const val currentlyReadingKey = "Currently Reading"
        const val haveReadKey = "Have Read"

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
                if (categories != null) {
                    putExtra(BookPage.categoriesKey, bookInfo.categories.toTypedArray())
                }
            }
            context.startActivity(launchBookPageIntent)
        }

        fun openBookReviewEdit(context: Context, bookReview: BookReview) {
            val launchBookReviewEditIntent = Intent(context, BookReviewEdit::class.java)
            launchBookReviewEditIntent.apply {
                putExtra(BookReviewEdit.textKey, bookReview.text)
                putExtra(BookReviewEdit.ratingKey, bookReview.rating)
                putExtra(BookReviewEdit.emailKey, bookReview.email)
                putExtra(BookReviewEdit.volumeIDKey, bookReview.volumeID)
                putExtra(BookReviewEdit.titleKey, bookReview.title)
                putExtra(BookReviewEdit.authorsKey, bookReview.authors.toTypedArray())
                putExtra(BookReviewEdit.firestoreIDKey, bookReview.firestoreID)
            }
            context.startActivity(launchBookReviewEditIntent)
        }

        fun openReadingList(context: Context, listName: String) {
            val launchReadingListIntent = Intent(context, ReadingList::class.java)
            launchReadingListIntent.apply {
                putExtra(ReadingList.listNameKey, listName)
            }
            context.startActivity(launchReadingListIntent)
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