package com.example.storyapp.utils

import com.example.storyapp.data.remote.response.AuthResponse
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.LoginResult
import com.example.storyapp.domain.model.AuthStatus
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.domain.model.User

object Mapping {

    fun userMapping(item: LoginResult): User = User(
        userId = item.userId, name = item.name, token = item.token
    )

    fun authStatus(item: AuthResponse): AuthStatus = AuthStatus(
        isError = item.error, message = item.message
    )

    fun storyMapping(item: List<ListStoryItem>): List<StoryEntity> = item.map { storyData ->
        StoryEntity(
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