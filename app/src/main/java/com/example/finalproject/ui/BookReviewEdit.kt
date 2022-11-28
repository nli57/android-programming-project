package com.example.finalproject.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.databinding.BookReviewEditBinding
import com.example.finalproject.model.BookReview
import com.google.firebase.Timestamp

class BookReviewEdit: AppCompatActivity() {
    companion object {
        const val textKey = "text"
        const val ratingKey = "rating"
        const val emailKey = "email"
        const val volumeIDKey = "volumeID"
        const val titleKey = "title"
        const val authorsKey = "authors"
        const val firestoreIDKey = "firestoreID"
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = BookReviewEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookReviewText = intent.extras?.getString(textKey)
        val bookReviewRating = intent.extras?.getFloat(ratingKey)
        val bookReviewEmail = intent.extras?.getString(emailKey)
        val bookReviewVolumeID = intent.extras?.getString(volumeIDKey)
        val bookReviewTitle = intent.extras?.getString(titleKey)
        val bookReviewAuthors = intent.extras?.getStringArray(authorsKey)
        val bookReviewFirestoreID = intent.extras?.getString(firestoreIDKey)

        // Initialize edit state
        binding.bookReviewET.setText(bookReviewText)
        binding.userBookReviewRating.rating = bookReviewRating!!

        binding.cancelBut.setOnClickListener {
            finish()
        }
        binding.saveBut.setOnClickListener {
            val newText = binding.bookReviewET.text.toString()
            val newRating = binding.userBookReviewRating.rating
            val bookReview = BookReview(
                text = newText,
                rating = newRating,
                email = bookReviewEmail!!,
                volumeID = bookReviewVolumeID!!,
                title = bookReviewTitle!!,
                authors = bookReviewAuthors!!.toList(),
                timeStamp = Timestamp.now(),
                firestoreID = bookReviewFirestoreID!!
            )
            viewModel.updateBookReview(bookReview)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }
}