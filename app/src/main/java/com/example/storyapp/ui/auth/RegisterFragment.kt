package com.example.storyapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.data.Results
import com.example.storyapp.databinding.FragmentRegisterBinding
import com.example.storyapp.utils.Constant
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playAnimation()
        checkRegister()

        binding.btnRegister.setOnClickListener {
            registerProcess()
        }
    }

    private fun checkRegister() {
        checkNameStatus()
        checkEmailStatus()
        checkPasswordStatus()
    }

    private fun checkNameStatus() {
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setButtonEnabled()
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun checkEmailStatus() {
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setButtonEnabled()
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun checkPasswordStatus() {
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setButtonEnabled()
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun setButtonEnabled() {
        binding.btnRegister.isEnabled =
            binding.etEmail.isFormValid && binding.etPassword.isFormValid && !binding.etName.isTextBlank
    }

    private fun registerProcess() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().lowercase().trim()
        val password = binding.etPassword.text.toString().trim()

        viewModel.registerProcess(name, email, password).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Results.Loading -> showLoading(true)
                    is Results.Success -> {
                        showLoading(false)
                        val registerStatus = result.data
                        if (!registerStatus) {
                            findNavController().popBackStack()
                        }
                    }

                    is Results.Error -> {
                        if (result.error == Constant.API_ERROR_BAD_REQUEST) showSnackBar(getString(R.string.email_already_used))
                        else showSnackBar(result.error)
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.ivLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.scrollLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilName, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(500)
        val registerButton =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                nameEditTextLayout, emailEditTextLayout, passwordEditTextLayout, registerButton
            )
            startDelay = 500
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}