package com.example.finalproject.ui.search

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.databinding.FragmentSearchBinding
import com.example.finalproject.ui.BookListingAdapter
import com.example.finalproject.ui.MainViewModel
import com.google.android.material.snackbar.Snackbar

class SearchFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentSearchBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BookListingAdapter(viewModel)
        binding.recyclerView.adapter = adapter
        val manager = LinearLayoutManager(this.context)
        binding.recyclerView.layoutManager = manager

        binding.searchBut.setOnClickListener {
            val searchTerm = binding.searchET.text.toString()
            if (searchTerm.isEmpty()) {
                Snackbar.make(
                    binding.searchET,
                    "Please enter a valid search input",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                hideKeyboard()
                viewModel.netBooks(searchTerm)
            }
        }
        binding.searchET.setOnEditorActionListener { /*v*/_, actionId, event ->
            if ((event != null
                        &&(event.action == KeyEvent.ACTION_DOWN)
                        &&(event.keyCode == KeyEvent.KEYCODE_ENTER))
                || (actionId == EditorInfo.IME_ACTION_DONE)) {
                hideKeyboard()
                binding.searchBut.callOnClick()
            }
            false
        }

        viewModel.observeBookListings().observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                Snackbar.make(
                    binding.searchET,
                    "No results found",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                adapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}