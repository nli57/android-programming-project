package com.example.finalproject.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.databinding.ActivityReadingListBinding

class ReadingList: AppCompatActivity() {
    companion object {
        const val listNameKey = "listName"
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityReadingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listName = intent.extras?.getString(listNameKey)

        // Set toolbar title to the name of the reading list
        supportActionBar?.title = listName!!

        val adapter = BookListingAdapter(viewModel, listName)
        binding.readingListRecyclerView.adapter = adapter
        val manager = LinearLayoutManager(this)
        binding.readingListRecyclerView.layoutManager = manager

        viewModel.observeReadingListBookListings().observe(this) {
            adapter.submitList(it)
        }

        // Initial fetch of books in reading list
        viewModel.setCurrReadingListName(listName!!)
        viewModel.fetchInitialReadingListBooksByListNameAndUID(viewModel.getUID())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }
}