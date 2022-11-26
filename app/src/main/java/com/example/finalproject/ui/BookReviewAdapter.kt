package com.example.finalproject.ui

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.BookReviewRowBinding
import com.example.finalproject.model.BookReview
import java.util.*

class BookReviewAdapter(private val viewModel: MainViewModel)
    : ListAdapter<BookReview, BookReviewAdapter.VH>(Diff()) {
    class Diff : DiffUtil.ItemCallback<BookReview>() {
        override fun areItemsTheSame(oldItem: BookReview, newItem: BookReview): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
        }

        override fun areContentsTheSame(oldItem: BookReview, newItem: BookReview): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
                    && oldItem.text == newItem.text
                    && oldItem.rating == newItem.rating
                    && oldItem.isbn == newItem.isbn
                    && oldItem.email == newItem.email
        }
    }

    private val dateFormat: DateFormat =
        SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.US)

    inner class VH(private val bookReviewRowBinding: BookReviewRowBinding) :
        RecyclerView.ViewHolder(bookReviewRowBinding.root) {

        fun bind(holder: VH, position: Int) {
            val bookReview = viewModel.getBookReview(position)
            holder.bookReviewRowBinding.bookReviewRating.rating = bookReview.rating
            holder.bookReviewRowBinding.bookReviewUserEmail.text = bookReview.email
            bookReview.timeStamp?.let {
                holder.bookReviewRowBinding.bookReviewTimestamp.text = dateFormat.format(it.toDate())
            }
            holder.bookReviewRowBinding.bookReviewText.text = bookReview.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val bookReviewRowBinding = BookReviewRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(bookReviewRowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(holder, position)
    }
}