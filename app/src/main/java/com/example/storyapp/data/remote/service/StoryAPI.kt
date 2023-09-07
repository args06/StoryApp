package com.example.storyapp.data.remote.service

import com.example.storyapp.data.remote.response.AuthResponse
import com.example.storyapp.data.remote.response.RetrieveResponse
import com.example.storyapp.utils.Constant
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface StoryAPI {

    @FormUrlEncoded
    @POST("login")
    suspend fun loginProcess(
        @Field("email") email: String,
        @Field("password") password: String
    ) : AuthResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun registerProcess(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : AuthResponse

    @GET("stories?size=${Constant.MAX_RESPONSE_SIZE}")
    suspend fun getStories(
        @Header("Authorization") auth: String
    ) : RetrieveResponse

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") caption: RequestBody,
    ) : AuthResponse
}