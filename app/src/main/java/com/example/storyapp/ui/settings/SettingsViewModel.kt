package com.example.storyapp.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.domain.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {

    fun clearLoginData() {
        viewModelScope.launch {
            repository.clearLoginData()
        }
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return repository.getThemeSettings()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            repository.saveThemeSetting(isDarkModeActive)
        }
    }
}