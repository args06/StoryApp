package com.example.storyapp.domain.repository

import androidx.lifecycle.LiveData
import com.example.storyapp.data.Results
import com.example.storyapp.domain.model.Story
import com.example.storyapp.domain.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface StoryRepository {

    fun loginProcess(email: String, password: String): LiveData<Results<Boolean>>

    fun registerProcess(name: String, email: String, password: String): LiveData<Results<Boolean>>

    fun getStories(token: String): LiveData<Results<List<Story>>>

    fun uploadImage(token: String, image: MultipartBody.Part, caption: RequestBody): LiveData<Results<Boolean>>

    suspend fun saveLoginData(user: User)

    suspend fun clearLoginData()

    fun getLoginStatus(): LiveData<Boolean>

    fun getUserSessionData(): LiveData<User>

    fun getThemeSettings(): LiveData<Boolean>

    suspend fun saveThemeSetting(isDarkModeActive: Boolean)
}