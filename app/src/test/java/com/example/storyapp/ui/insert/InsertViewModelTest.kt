package com.example.storyapp.ui.insert

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.data.Results
import com.example.storyapp.domain.repository.StoryRepository
import com.example.storyapp.ui.dashboard.DashboardViewModel
import com.example.storyapp.utils.DataDummy
import com.example.storyapp.utils.Helper
import com.example.storyapp.utils.LiveDataTestUtil.getOrAwaitValue
import com.example.storyapp.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class InsertViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    private lateinit var insertViewModel: InsertViewModel

    @Before
    fun setUp() {
        insertViewModel = InsertViewModel(storyRepository)
    }

    @Test
    fun `when success add Story`() {
        val dummyToken = Helper.constructAuthToken("testToken")

        val dummyFile = File("file.jpg")
        val requestImageFile = dummyFile.asRequestBody("image/jpeg".toMediaType())
        val dummyImage: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo", dummyFile.name, requestImageFile
        )

        val dummyCaption = "Caption".toRequestBody("text/plain".toMediaType())
        val dummyLocation = 121.0f

        val dataDummy = Results.Success(
            data = true
        )
        val expectedResult = MutableLiveData<Results<Boolean>>()
        expectedResult.value = dataDummy

        Mockito.`when`(insertViewModel.uploadImage(dummyToken, dummyImage, dummyCaption, dummyLocation, dummyLocation))
            .thenReturn(expectedResult)

        val actualResult: Results<Boolean> = insertViewModel.uploadImage(
            dummyToken, dummyImage, dummyCaption, dummyLocation, dummyLocation
        ).getOrAwaitValue()

        assertEquals(expectedResult.value, actualResult)
    }

}