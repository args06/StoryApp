package com.example.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.domain.repository.StoryRepository
import com.example.storyapp.utils.Helper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {

    fun getThemeSettings(): LiveData<Boolean> {
        return repository.getThemeSettings()
    }

    fun getUserSessionData() = repository.getUserSessionData()

    fun getAllStoryWithLocation(token: String) =
        repository.getAllStoryWithLocation(Helper.constructAuthToken(token))
}