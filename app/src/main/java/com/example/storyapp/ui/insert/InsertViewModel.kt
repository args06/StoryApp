package com.example.storyapp.ui.insert

import androidx.lifecycle.ViewModel
import com.example.storyapp.domain.repository.StoryRepository
import com.example.storyapp.utils.Helper
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class InsertViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {

    fun getUserSessionData() = repository.getUserSessionData()

    fun uploadImage(
        token: String,
        image: MultipartBody.Part,
        caption: RequestBody,
        latitude: Float?,
        longitude: Float?
    ) = repository.uploadImage(
        Helper.constructAuthToken(token), image, caption, latitude, longitude
    )
}