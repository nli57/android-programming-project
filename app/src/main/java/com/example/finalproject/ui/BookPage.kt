package com.example.finalproject.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.databinding.ActivityBookPageBinding
import com.example.finalproject.glide.Glide

class BookPage: AppCompatActivity() {
    companion object {
        const val titleKey = "title"
        const val authorsKey = "authors"
        const val descriptionKey = "description"
        const val imageLinkKey = "imageLink"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBookPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.extras?.getString(titleKey)
        val authors = intent.extras?.getStringArray(authorsKey)
        val description = intent.extras?.getString(descriptionKey)
        val imageLink = intent.extras?.getString(imageLinkKey)

        binding.bookPageTitle.text = title
        if (authors != null) {
            binding.bookPageAuthor.text = formatAuthors(authors)
        }
        binding.bookPageDescription.text = description
        if (imageLink != null) {
            Glide.glideFetch(
                imageLink.replace("http", "https"),
                binding.bookPagePic
            )
        }
    }

    private fun formatAuthors(authors: Array<String>) : String {
        return if (authors.size == 1) {
            "By ${authors[0]}"
        } else {    // >1 author
            var authorsStr = authors[0]
            for (i in 1 until authors.size) {
                authorsStr += ", ${authors[i]}"
            }
            "By $authorsStr"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }
}