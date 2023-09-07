package com.example.storyapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.domain.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {

    var splashScreenLoading: Boolean = true

    fun loginProcess(email: String, password: String) = repository.loginProcess(email, password)

    fun registerProcess(name: String, email: String, password: String) =
        repository.registerProcess(name, email, password)

    fun getLoginStatus() = repository.getLoginStatus()

    fun getThemeSettings(): LiveData<Boolean> {
        return repository.getThemeSettings()
    }
}