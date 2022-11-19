package com.example.finalproject.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.databinding.ActivityBookPageBinding
import com.example.finalproject.glide.Glide

class BookPage: AppCompatActivity() {
    companion object {
        const val titleKey = "title"
        const val subtitleKey = "subtitle"
        const val authorsKey = "authors"
        const val descriptionKey = "description"
        const val imageLinkKey = "imageLink"
        const val isbn10Key = "ISBN-10"
        const val isbn13Key = "ISBN-13"
        const val publisherKey = "publisher"
        const val publishedDateKey = "publishedDate"
        const val pageCountKey = "pageCount"
        const val categoriesKey = "categories"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBookPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.extras?.getString(titleKey)
        val subtitle = intent.extras?.getString(subtitleKey)
        val authors = intent.extras?.getStringArray(authorsKey)
        val description = intent.extras?.getString(descriptionKey)
        val imageLink = intent.extras?.getString(imageLinkKey)
        val isbn10 = intent.extras?.getString(isbn10Key)
        val isbn13 = intent.extras?.getString(isbn13Key)
        val publisher = intent.extras?.getString(publisherKey)
        val publishedDate = intent.extras?.getString(publishedDateKey)
        val pageCount = intent.extras?.getInt(pageCountKey)
        val categories = intent.extras?.getStringArray(categoriesKey)

        binding.bookPageTitle.text = title
        binding.bookPageSubtitle.text = subtitle
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
        binding.bookPageISBN10.text = "ISBN-10: $isbn10"
        binding.bookPageISBN13.text = "ISBN-13: $isbn13"
        binding.bookPagePublisher.text = "Publisher: $publisher"
        binding.bookPagePublishedDate.text = "Publication Date: $publishedDate"
        binding.bookPagePageCount.text = "Page Count: $pageCount"
        if (categories != null) {
            binding.bookPageCategories.text = formatCategories(categories)
        }
    }

    private fun formatAuthors(authors: Array<String>) : String {
        return if (authors == null || authors.isEmpty()) {
            ""
        } else if (authors.size == 1) {
            "By ${authors[0]}"
        } else {    // >1 author
            var authorsStr = authors[0]
            for (i in 1 until authors.size) {
                authorsStr += ", ${authors[i]}"
            }
            "By $authorsStr"
        }
    }

    private fun formatCategories(categories: Array<String>) : String {
        return if (categories == null || categories.isEmpty()) {
            "Categories: N/A"
        } else if (categories.size == 1) {
            "Categories: ${categories[0]}"
        } else {    // >1 author
            var categoriesStr = categories[0]
            for (i in 1 until categories.size) {
                categoriesStr += ", ${categories[i]}"
            }
            "Categories: $categoriesStr"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }
}