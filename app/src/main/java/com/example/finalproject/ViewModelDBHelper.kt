package com.example.finalproject

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.finalproject.model.BookReview
import com.example.finalproject.model.ReadingListBook
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ViewModelDBHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val bookReviewCollectionRoot = "allBookReviews"
    private val readingListCollectionRoot = "allReadingListBooks"

    private fun ellipsizeString(string: String) : String {
        if (string.length < 10)
            return string
        return string.substring(0..9) + "..."
    }

    // BOOK REVIEWS
    private fun dbFetchBookReviews(
        bookReviewList: MutableLiveData<List<BookReview>>,
        volumeID: String
    ) {
        db.collection(bookReviewCollectionRoot)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .whereEqualTo("volumeID", volumeID)
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allBookReviews fetch ${result!!.documents.size}")
                bookReviewList.postValue(result.documents.mapNotNull {
                    it.toObject(BookReview::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "allBookReviews fetch FAILED ", it)
            }
    }

    private fun dbFetchUserBookReviews(
        userBookReviewList: MutableLiveData<List<BookReview>>,
        email: String
    ) {
        db.collection(bookReviewCollectionRoot)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .whereEqualTo("email", email)
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allBookReviews fetch ${result!!.documents.size}")
                userBookReviewList.postValue(result.documents.mapNotNull {
                    it.toObject(BookReview::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "allBookReviews fetch FAILED ", it)
            }
    }

    fun fetchInitialBookReviewsByVolumeID(
        bookReviewList: MutableLiveData<List<BookReview>>,
        volumeID: String
    ) {
        dbFetchBookReviews(bookReviewList, volumeID)
    }

    fun fetchInitialBookReviewsByEmail(
        userBookReviewList: MutableLiveData<List<BookReview>>,
        email: String
    ) {
        dbFetchUserBookReviews(userBookReviewList, email)
    }

    fun createBookReview(
        bookReview: BookReview,
        bookReviewList: MutableLiveData<List<BookReview>>,
        userBookReviewList: MutableLiveData<List<BookReview>>
    ) {
        db.collection(bookReviewCollectionRoot)
            .add(bookReview)
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "BookReview create \"${ellipsizeString(bookReview.text)}\" id: ${bookReview.firestoreID}"
                )
                dbFetchBookReviews(bookReviewList, bookReview.volumeID)
                dbFetchUserBookReviews(userBookReviewList, bookReview.email)
            }
            .addOnFailureListener { e ->
                Log.d(javaClass.simpleName, "BookReview create FAILED \"${ellipsizeString(bookReview.text)}\"")
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    fun updateBookReview(
        bookReview: BookReview,
        bookReviewList: MutableLiveData<List<BookReview>>,
        userBookReviewList: MutableLiveData<List<BookReview>>
    ) {
        db.collection(bookReviewCollectionRoot)
            .document(bookReview.firestoreID)
            .set(bookReview)
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "BookReview update \"${ellipsizeString(bookReview.text)}\" id: ${bookReview.firestoreID}"
                )
                dbFetchBookReviews(bookReviewList, bookReview.volumeID)
                dbFetchUserBookReviews(userBookReviewList, bookReview.email)
            }
            .addOnFailureListener { e ->
                Log.d(javaClass.simpleName, "BookReview update FAILED \"${ellipsizeString(bookReview.text)}\"")
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    fun deleteBookReview(
        bookReview: BookReview,
        bookReviewList: MutableLiveData<List<BookReview>>,
        userBookReviewList: MutableLiveData<List<BookReview>>
    ) {
        db.collection(bookReviewCollectionRoot)
            .document(bookReview.firestoreID)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "BookReview delete \"${ellipsizeString(bookReview.text)}\" id: ${bookReview.firestoreID}"
                )
                dbFetchBookReviews(bookReviewList, bookReview.volumeID)
                dbFetchUserBookReviews(userBookReviewList, bookReview.email)
            }
            .addOnFailureListener { e ->
                Log.d(javaClass.simpleName, "BookReview delete FAILED \"${ellipsizeString(bookReview.text)}\"")
                Log.w(javaClass.simpleName, "Error deleting document", e)
            }
    }

    // READING LISTS
    private fun dbFetchReadingListBooks(
        readingListBookList: MutableLiveData<List<ReadingListBook>>,
        displayListName: String,
        uid: String
    ) {
        db.collection(readingListCollectionRoot)
            .orderBy("timeStamp")
            .whereEqualTo("listName", displayListName)
            .whereEqualTo("uid", uid)
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allReadingListBooks fetch ${result!!.documents.size}")
                readingListBookList.postValue(result.documents.mapNotNull {
                    it.toObject(ReadingListBook::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "allReadingListBooks fetch FAILED ", it)
            }
    }

    fun fetchInitialReadingListBooksByListNameAndUID(
        readingListBookList: MutableLiveData<List<ReadingListBook>>,
        displayListName: String,
        uid: String
    ) {
        dbFetchReadingListBooks(readingListBookList, displayListName, uid)
    }

    fun addBookToReadingList(
        readingListBook: ReadingListBook,
        readingListBookList: MutableLiveData<List<ReadingListBook>>,
        displayListName: String
    ) {
        db.collection(readingListCollectionRoot)
            .add(readingListBook)
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "ReadingListBook create id: ${readingListBook.firestoreID}"
                )
                dbFetchReadingListBooks(
                    readingListBookList,
                    displayListName,
                    readingListBook.uid
                )
            }
            .addOnFailureListener { e ->
                Log.d(javaClass.simpleName, "ReadingListBook create FAILED id: ${readingListBook.volumeID}")
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    fun removeBookFromReadingList(
        readingListBook: ReadingListBook,
        readingListBookList: MutableLiveData<List<ReadingListBook>>,
        displayListName: String
    ) {
        db.collection(readingListCollectionRoot)
            .document(readingListBook.firestoreID)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "ReadingListBook delete id: ${readingListBook.firestoreID}"
                )
                dbFetchReadingListBooks(
                    readingListBookList,
                    displayListName,
                    readingListBook.uid
                )
            }
            .addOnFailureListener { e ->
                Log.d(
                    javaClass.simpleName,
                    "ReadingListBook delete FAILED id: ${readingListBook.volumeID}"
                )
                Log.w(javaClass.simpleName, "Error deleting document", e)
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
        readingListBook: ReadingListBook? = null,
    ) {
        db.collection(readingListCollectionRoot)
            .orderBy("timeStamp")
            .whereEqualTo("volumeID", volumeID)
            .whereEqualTo("listName", listName)
            .whereEqualTo("uid", uid)
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allReadingListBooks fetch ${result!!.documents.size}")
                if (result!!.documents.size > 0) {
                    // Don't update if the book already exists in the specified reading list
                    updateReadingListBookFailure(view)
                } else {
                    updateReadingListBookSuccess(volumeID, listName, readingListBook, view)
                }
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "allReadingListBooks fetch FAILED ", it)
            }
    }
}