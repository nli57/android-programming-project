package com.example.finalproject.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentLoginBinding
import com.example.finalproject.ui.BookReviewAdapter
import com.example.finalproject.ui.MainViewModel

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
                binding.userBookReviewRecyclerView.visibility = View.GONE
            } else {
                binding.bookReviewsArrow.setImageResource(
                    R.drawable.ic_baseline_keyboard_arrow_down_24
                )
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
        viewModel.observeUserBookReviews().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        // Initial fetch of user book reviews
        viewModel.fetchInitialBookReviewsByEmail(viewModel.getEmail())
    }
}