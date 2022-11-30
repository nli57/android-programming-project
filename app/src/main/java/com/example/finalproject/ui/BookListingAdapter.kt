package com.example.finalproject.ui

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.glide.Glide
import com.example.finalproject.api.BookInfo
import com.example.finalproject.databinding.RowListingBinding
import com.example.finalproject.model.ReadingListBook
import com.google.android.material.snackbar.Snackbar

class BookListingAdapter(
        private val viewModel: MainViewModel,
        private val listName: String? = null
    ) : ListAdapter<BookInfo, BookListingAdapter.VH>(BookDiff()) {

    private val readingListMoveToMap = hashMapOf(
        MainViewModel.wantToReadKey to
                arrayOf(MainViewModel.currentlyReadingKey, MainViewModel.haveReadKey),
        MainViewModel.currentlyReadingKey to
                arrayOf(MainViewModel.wantToReadKey, MainViewModel.haveReadKey),
        MainViewModel.haveReadKey to
                arrayOf(MainViewModel.wantToReadKey, MainViewModel.currentlyReadingKey)
    )

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
        bookListingBinding.rowBookTitle.setOnClickListener {
            MainViewModel.openBookPage(it.context, item)
        }
        bookListingBinding.rowBookSubtitle.text = item.subtitle
        bookListingBinding.rowAuthor.text = formatAuthors(item.authors)
        if (item.imageLinks != null && item.imageLinks.smallThumbnail != null) {
            Glide.glideFetch(
                item.imageLinks.smallThumbnail.replace("http", "https"),
                bookListingBinding.rowPic
            )
        }

        // If this is a reading list book listing, then allow the user to delete/move books
        // (otherwise don't show the options menu)
        if (listName != null) {
            bookListingBinding.hamburgerBut.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, bookListingBinding.hamburgerBut)
                popupMenu.inflate(R.menu.reading_list_menu)
                updateMenuTitles(popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    val moveToOptions = readingListMoveToMap[listName]
                    if (moveToOptions != null) {
                        val id = menuItem.itemId
                        val readingListBook = viewModel.getReadingListBook(position)

                        if (id == R.id.moveOption1) {
                            moveBookToNewReadingList(
                                readingListBook,
                                moveToOptions[0],
                                bookListingBinding.hamburgerBut
                            )
                            true
                        } else if (id == R.id.moveOption2) {
                            moveBookToNewReadingList(
                                readingListBook,
                                moveToOptions[1],
                                bookListingBinding.hamburgerBut
                            )
                            true
                        } else if (id == R.id.deleteOption) {
                            viewModel.removeBookFromReadingList(readingListBook)
                            true
                        } else {
                            false
                        }
                    } else {
                        false
                    }
                }
                popupMenu.show()
            }
        } else {
            bookListingBinding.hamburgerBut.visibility = View.GONE
        }
    }

    private fun formatAuthors(authors: List<String>) : String {
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

    private fun updateMenuTitles(menu: Menu) {
        if (listName != null) {
            val moveToOptions = readingListMoveToMap[listName]
            if (moveToOptions != null) {
                menu.getItem(0).title = "Move to ${moveToOptions[0]}"
                menu.getItem(1).title = "Move to ${moveToOptions[1]}"
            }
        }
    }

    private fun moveBookToNewReadingList(
        readingListBook: ReadingListBook,
        moveToListName: String,
        view: View
    ) {
        viewModel.updateReadingListBook(
            readingListBook.volumeID,
            moveToListName,
            viewModel.getUID(),
            view,
            ::updateReadingListBookSuccess,
            ::updateReadingListBookFailure,
            readingListBook
        )
    }

    private fun updateReadingListBookSuccess(
        volumeID: String,
        moveToListName: String,
        readingListBook: ReadingListBook?,
        view: View
    ) {
        viewModel.addBookToReadingList(volumeID, moveToListName)
        viewModel.removeBookFromReadingList(readingListBook!!)
        Snackbar.make(
            view,
            "This book has been moved to the new reading list",
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun updateReadingListBookFailure(view: View) {
        Snackbar.make(
            view,
            "This book cannot be moved because it already exists in that reading list",
            Snackbar.LENGTH_LONG
        ).show()
    }

    class BookDiff : DiffUtil.ItemCallback<BookInfo>() {
        override fun areItemsTheSame(oldItem: BookInfo, newItem: BookInfo): Boolean {
            return oldItem.volumeID == newItem.volumeID
        }

        override fun areContentsTheSame(oldItem: BookInfo, newItem: BookInfo): Boolean {
            return oldItem.volumeID == newItem.volumeID &&
                    oldItem.title == newItem.title &&
                    oldItem.authors == newItem.authors &&
                    oldItem.imageLinks == newItem.imageLinks
        }
    }
}