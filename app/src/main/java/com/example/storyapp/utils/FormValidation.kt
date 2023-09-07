package com.example.storyapp.utils

object FormValidation {

    fun isEmailValid(text: String): Boolean {
        return text.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }
}