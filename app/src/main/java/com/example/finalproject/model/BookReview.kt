package com.example.finalproject.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class BookReview(
    var text: String = "",
    var rating: Float = 0f,
    var email: String = "",
    var volumeID: String = "",
    var title: String = "",
    var authors: List<String> = listOf(),
    // Written on the server
    @ServerTimestamp val timeStamp: Timestamp? = null,
    // firestoreID is generated by firestore, used as primary key
    @DocumentId var firestoreID: String = ""
)
