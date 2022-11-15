package com.example.finalproject.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.glide.Glide
import com.example.finalproject.api.BookInfo
import com.example.finalproject.databinding.RowListingBinding

class BookListingAdapter(private val viewModel: MainViewModel)
    : ListAdapter<BookInfo, BookListingAdapter.VH>(BookDiff()) {

    inner class VH(val bookListingBinding: RowListingBinding)
        : RecyclerView.ViewHolder(bookListingBinding.root) {
        init {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val bookListingBinding = RowListingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(bookListingBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(holder.adapterPosition)
        val bookListingBinding = holder.bookListingBinding

        bookListingBinding.rowBookTitle.text = item.title
        bookListingBinding.rowAuthor.text = formatAuthors(item.authors)
        Glide.glideFetch(
            item.imageLinks.smallThumbnail.replace("http", "https"),
            bookListingBinding.rowPic
        )
    }

    private fun formatAuthors(authors: List<String>) : String {
        return if (authors.size == 1) {
            "Author: ${authors[0]}"
        } else {    // >1 author
            var authorsStr = authors[0]
            for (i in 1 until authors.size) {
                authorsStr += ", ${authors[i]}"
            }
            "Authors: $authorsStr"
        }
    }

    class BookDiff : DiffUtil.ItemCallback<BookInfo>() {
        override fun areItemsTheSame(oldItem: BookInfo, newItem: BookInfo): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: BookInfo, newItem: BookInfo): Boolean {
            return oldItem.title == newItem.title &&
                    oldItem.authors == newItem.authors &&
                    oldItem.imageLinks == newItem.imageLinks
        }
    }
}