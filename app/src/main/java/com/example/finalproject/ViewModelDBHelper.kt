package com.example.finalproject

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.finalproject.model.BookReview
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ViewModelDBHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionRoot = "allBookReviews"

    private fun ellipsizeString(string: String) : String {
        if (string.length < 10)
            return string
        return string.substring(0..9) + "..."
    }

    private fun dbFetchBookReviews(
        bookReviewList: MutableLiveData<List<BookReview>>,
        volumeID: String
    ) {
        db.collection(collectionRoot)
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
        db.collection(collectionRoot)
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
        db.collection(collectionRoot)
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
}