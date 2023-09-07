package com.example.storyapp.utils

import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.AuthResponse
import com.example.storyapp.data.remote.response.LoginResult
import com.example.storyapp.domain.model.AuthStatus
import com.example.storyapp.domain.model.Story
import com.example.storyapp.domain.model.User

object Mapping {

    fun userMapping(item: LoginResult) : User = User(
        userId = item.userId.toString(),
        name = item.name.toString(),
        token = item.token.toString()
    )

    fun authStatus(item: AuthResponse) : AuthStatus = AuthStatus(
        isError = item.error ?: true,
        message = item.message.toString()
    )

    fun storyMapping(item: List<ListStoryItem>) : List<Story> = item.map { storyData ->
        Story(
            id = storyData.id,
            name = storyData.name,
            description = storyData.description,
            photoUrl = storyData.photoUrl,
            createdAt = storyData.createdAt,
            lat = storyData.lat,
            lon = storyData.lon
        )
    }
}