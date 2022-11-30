package com.example.finalproject.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentLoginBinding
import com.example.finalproject.ui.BookReviewAdapter
import com.example.finalproject.ui.MainViewModel
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {
    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val bookReviewsSortCategories: Array<String> by lazy {
        resources.getStringArray(R.array.userBookReviewsSort)
    }
    private val bookReviewsSortDirections: Array<String> by lazy {
        resources.getStringArray(R.array.bookReviewsSortDirection)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userName.text = "Welcome, ${viewModel.getDisplayName()}"

        val adapter = BookReviewAdapter(viewModel, MainViewModel.userBookReviewKey)
        binding.userBookReviewRecyclerView.adapter = adapter
        val manager = LinearLayoutManager(this.context)
        binding.userBookReviewRecyclerView.layoutManager = manager

        // Toggle visibility of reading lists and book reviews
        binding.readingListsTV.setOnClickListener {
            if (binding.wantToReadReadingList.visibility == View.VISIBLE) {
                binding.readingsListsArrow.setImageResource(
                    R.drawable.ic_baseline_chevron_right_24
                )
                binding.wantToReadReadingList.visibility = View.GONE
                binding.currentlyReadingReadingList.visibility = View.GONE
                binding.haveReadReadingList.visibility = View.GONE
            } else {
                binding.readingsListsArrow.setImageResource(
                    R.drawable.ic_baseline_keyboard_arrow_down_24
                )
                binding.wantToReadReadingList.visibility = View.VISIBLE
                binding.currentlyReadingReadingList.visibility = View.VISIBLE
                binding.haveReadReadingList.visibility = View.VISIBLE
            }
        }
        binding.bookReviewsTV.setOnClickListener {
            if (binding.userBookReviewRecyclerView.visibility == View.VISIBLE) {
                binding.bookReviewsArrow.setImageResource(
                    R.drawable.ic_baseline_chevron_right_24
                )
                binding.bookReviewsSortSpinner.visibility = View.GONE
                binding.bookReviewsSortDirectionSpinner.visibility = View.GONE
                binding.bookReviewsSortSubmitBut.visibility = View.GONE
                binding.userBookReviewRecyclerView.visibility = View.GONE
            } else {
                binding.bookReviewsArrow.setImageResource(
                    R.drawable.ic_baseline_keyboard_arrow_down_24
                )
                binding.bookReviewsSortSpinner.visibility = View.VISIBLE
                binding.bookReviewsSortDirectionSpinner.visibility = View.VISIBLE
                binding.bookReviewsSortSubmitBut.visibility = View.VISIBLE
                binding.userBookReviewRecyclerView.visibility = View.VISIBLE
            }
        }

        // Reading lists
        binding.wantToReadReadingList.setOnClickListener {
            MainViewModel.openReadingList(it.context, MainViewModel.wantToReadKey)
        }
        binding.currentlyReadingReadingList.setOnClickListener {
            MainViewModel.openReadingList(it.context, MainViewModel.currentlyReadingKey)
        }
        binding.haveReadReadingList.setOnClickListener {
            MainViewModel.openReadingList(it.context, MainViewModel.haveReadKey)
        }

        // Book reviews
        val bookReviewsSortAdapter = ArrayAdapter.createFromResource(
            view.context,
            R.array.userBookReviewsSort,
            android.R.layout.simple_spinner_item
        )
        bookReviewsSortAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.bookReviewsSortSpinner.adapter = bookReviewsSortAdapter

        val bookReviewsSortDirectionAdapter = ArrayAdapter.createFromResource(
            view.context,
            R.array.bookReviewsSortDirection,
            android.R.layout.simple_spinner_item
        )
        bookReviewsSortDirectionAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.bookReviewsSortDirectionSpinner.adapter = bookReviewsSortDirectionAdapter

        binding.bookReviewsSortSubmitBut.setOnClickListener {
            var bookReviewsSortVal : String? = null
            val bookReviewsSortPos = binding.bookReviewsSortSpinner.selectedItemPosition
            if (bookReviewsSortPos != 0) {
                bookReviewsSortVal = bookReviewsSortCategories[bookReviewsSortPos]
            }

            val bookReviewsSortDirectionPos = binding.bookReviewsSortDirectionSpinner
                .selectedItemPosition
            val bookReviewsSortDirectionVal = bookReviewsSortDirections[
                    bookReviewsSortDirectionPos
            ]

            if (bookReviewsSortVal == null) {
                Snackbar.make(
                    binding.bookReviewsSortSpinner,
                    "Please select a valid category to sort by",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                viewModel.sortBookReviews(
                    bookReviewsSortVal,
                    bookReviewsSortDirectionVal,
                    MainViewModel.userBookReviewKey
                )
            }
        }

        viewModel.observeUserBookReviews().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        // Initial fetch of user book reviews
        viewModel.fetchInitialBookReviewsByEmail(viewModel.getEmail())
    }
}