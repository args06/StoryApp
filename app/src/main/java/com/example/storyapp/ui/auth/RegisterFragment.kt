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
import com.example.storyapp.databinding.FragmentRegisterBinding
import com.example.storyapp.utils.Constant
import com.example.storyapp.utils.FormValidation
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            checkRegister()
        }
    }

    private fun checkRegister() {
        var isError = false
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().lowercase().trim()
        val password = binding.etPassword.text.toString().trim()

        if (name.isEmpty()) {
            isError = true
            binding.etName.error = getString(R.string.form_empty_message)
        }
        if (email.isEmpty()) {
            isError = true
            binding.etEmail.error = getString(R.string.form_empty_message)
        } else if (!FormValidation.isEmailValid(email)) {
            isError = true
            binding.etEmail.error = getString(R.string.incorrect_email_format)
        }
        if (password.isEmpty()) {
            isError = true
            binding.etPassword.error = getString(R.string.form_empty_message)
        }

        if (!isError) {
            registerProcess(name, email, password)
        }
    }

    private fun registerProcess(
        name: String, email: String, password: String
    ) {
        viewModel.registerProcess(name, email, password).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when(result) {
                    is Results.Loading -> showLoading(true)
                    is Results.Success -> {
                        showLoading(false)
                        val registerStatus = result.data
                        if (!registerStatus) {
                            findNavController().popBackStack()
                        }
                    }
                    is Results.Error -> {
                        if (result.error == Constant.API_ERROR_BAD_REQUEST)
                            showSnackBar(getString(R.string.email_already_used))
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