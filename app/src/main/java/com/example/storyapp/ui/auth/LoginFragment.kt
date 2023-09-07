package com.example.storyapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.data.Results
import com.example.storyapp.databinding.FragmentLoginBinding
import com.example.storyapp.utils.Constant
import com.example.storyapp.utils.FormValidation
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            checkLogin()
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun checkLogin() {
        var isError = false
        val email = binding.etEmail.text.toString().lowercase().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            isError = true
            binding.etEmail.error = getString(R.string.form_empty_message)
        }
        if (!FormValidation.isEmailValid(email)) {
            isError = true
            binding.etEmail.error = getString(R.string.incorrect_email_format)
        }
        if (password.isEmpty()) {
            isError = true
            binding.etPassword.error = getString(R.string.form_empty_message)
        }

        if (!isError) {
            loginProcess(email, password)
        }
    }

    private fun loginProcess(
        email: String, password: String
    ) {
        viewModel.loginProcess(email, password).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when(result) {
                    is Results.Loading -> showLoading(true)
                    is Results.Success -> {
                        showLoading(false)
                        val loginStatus = result.data
                        if (loginStatus) {
                            val toMainActivity =
                                LoginFragmentDirections.actionLoginFragmentToMainActivity()
                            findNavController().navigate(toMainActivity)
                            requireActivity().finish()
                        }
                    }
                    is Results.Error -> {
                        if (result.error == Constant.API_ERROR_UNAUTHORIZED)
                            showSnackBar(getString(R.string.incorrect_email_or_password))
                        else
                            showSnackBar(result.error)
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.ivLoading.visibility = View.VISIBLE
        } else {
            binding.ivLoading.visibility = View.GONE
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.scrollLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}