package com.example.storyapp.ui.dashboard

import androidx.lifecycle.ViewModel
import com.example.storyapp.domain.repository.StoryRepository
import com.example.storyapp.utils.Helper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: StoryRepository
): ViewModel() {

    fun getUserSessionData() = repository.getUserSessionData()

    fun getStories(token: String) = repository.getStories(Helper.constructAuthToken(token))
}