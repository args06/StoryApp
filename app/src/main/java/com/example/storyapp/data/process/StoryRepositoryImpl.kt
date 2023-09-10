package com.example.storyapp.data.process

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.Results
import com.example.storyapp.data.preferences.AppPreferences
import com.example.storyapp.data.remote.service.StoryAPI
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.data.local.service.StoryDatabase
import com.example.storyapp.domain.model.User
import com.example.storyapp.domain.repository.StoryRepository
import com.example.storyapp.utils.Mapping
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val apiService: StoryAPI,
    private val pref: AppPreferences,
    private val storyDatabase: StoryDatabase
) : StoryRepository {

    override fun loginProcess(email: String, password: String): LiveData<Results<Boolean>> =
        liveData {
            emit(Results.Loading)
            try {
                val response = apiService.loginProcess(email, password)
                if (response.loginResult != null) {
                    val loginStatus = Mapping.authStatus(response)

                    if (!loginStatus.isError) {
                        val user = Mapping.userMapping(response.loginResult)
                        saveLoginData(user)
                    }
                    emit(Results.Success(true))
                }
            } catch (e: Exception) {
                emit(Results.Error((e as HttpException).code().toString()))
            }
        }

    override fun registerProcess(
        name: String, email: String, password: String
    ): LiveData<Results<Boolean>> = liveData {
        emit(Results.Loading)
        try {
            val response = apiService.registerProcess(name, email, password)
            val registerStatus = Mapping.authStatus(response)
            emit(Results.Success(registerStatus.isError))
        } catch (e: Exception) {
            emit(Results.Error((e as HttpException).code().toString()))
        }
    }

//    override fun getStories(token: String): LiveData<Results<List<StoryEntity>>> = liveData {
//        emit(Results.Loading)
//        try {
//            val response = apiService.getStories(token)
//            if (response.listStory != null) {
//                val listStory = Mapping.storyMapping(response.listStory)
//                emit(Results.Success(listStory))
//            }
//        } catch (e: Exception) {
//            emit(Results.Error((e as HttpException).code().toString()))
//        }
//    }

    override fun getStories(token: String): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class) return Pager(config = PagingConfig(
            pageSize = 5
        ), remoteMediator = StoryRemoteMediator(
            storyAPI = apiService, storyDatabase = storyDatabase, authToken = token
        ), pagingSourceFactory = {
            storyDatabase.storyDao().getAllStory()
        }).liveData
    }

    override fun uploadImage(token: String, image: MultipartBody.Part, caption: RequestBody) =
        liveData {
            emit(Results.Loading)
            try {
                val response = apiService.uploadImage(token, image, caption)
                val registerStatus = Mapping.authStatus(response)
                emit(Results.Success(registerStatus.isError))

            } catch (e: Exception) {
                emit(Results.Error((e as HttpException).code().toString()))
            }
        }

    override fun getUserSessionData(): LiveData<User> {
        return pref.getUserSessionData().asLiveData()
    }

    override suspend fun saveLoginData(user: User) {
        pref.saveUserSession(user)
    }

    override suspend fun clearLoginData() {
        pref.clearUserSession()
    }

    override fun getLoginStatus(): LiveData<Boolean> {
        return pref.getLoginStatus().asLiveData()
    }

    override fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    override suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        pref.saveThemeSetting(isDarkModeActive)
    }
}