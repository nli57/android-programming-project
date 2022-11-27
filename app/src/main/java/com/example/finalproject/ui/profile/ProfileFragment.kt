package com.example.finalproject.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.finalproject.AuthInit
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentProfileBinding
import com.example.finalproject.ui.MainViewModel
import com.example.finalproject.ui.login.LoginFragment
import com.example.finalproject.ui.login.LogoutFragment
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract

class ProfileFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
            viewModel.updateUser()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginLogoutBut.setOnClickListener {
            if (viewModel.getLoginStatus()) {
                viewModel.signOut()
            } else {
                AuthInit(viewModel, signInLauncher)
            }
        }

        viewModel.observeLoginStatus().observe(viewLifecycleOwner) {
            if (it) {
                binding.loginLogoutBut.text = "Logout"
                parentFragmentManager.commit {
                    replace(R.id.loginContainer, LoginFragment.newInstance())
                }
            } else {
                binding.loginLogoutBut.text = "Login"
                parentFragmentManager.commit {
                    replace(R.id.loginContainer, LogoutFragment.newInstance())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}