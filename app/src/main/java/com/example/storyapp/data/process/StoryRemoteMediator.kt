package com.example.storyapp.data.process

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.storyapp.data.local.entity.RemoteKeys
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.data.local.service.StoryDatabase
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.service.StoryAPI
import com.example.storyapp.utils.Constant
import com.example.storyapp.utils.Mapping

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val storyAPI: StoryAPI,
    private val storyDatabase: StoryDatabase,
    private val authToken: String
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: Constant.INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        try {
            val responseData = storyAPI.getStories(authToken, page, state.config.pageSize)

            val listStory = Mapping.storyMapping(responseData.listStory as List<ListStoryItem>)
            val endOfPaginationReached = listStory.isEmpty()

            storyDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    storyDatabase.remoteKeysDao().deleteRemoteKeys()
                    storyDatabase.storyDao().deleteAllStory()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = listStory.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                storyDatabase.remoteKeysDao().insertAll(keys)
                storyDatabase.storyDao().insertStory(listStory)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyDatabase.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

}