package com.example.submissionappstory.data.local.pagedir

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.submissionappstory.data.local.room.StoryDb
import com.example.submissionappstory.data.remote.apiresponse.ListStory
import com.example.submissionappstory.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PageMediator @Inject constructor(
    private val storyDb: StoryDb,
    private val apiService: ApiService,
    private val tokenPref: TokenPreferences
): RemoteMediator<Int, ListStory>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStory>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: initialPageIndex
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyFirstItem(state)
                val prevKeys = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKeys
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }
        return try {
            val token: String = tokenPref.getToken().first()
            val response = apiService.getStory("Bearer $token", state.config.pageSize, page)
            val endOfPaginationReach = response.listStory.isEmpty()

            storyDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    storyDb.remoteKeysDao().delRemote()
                    storyDb.storyDao().deleteStory()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReach) null else page + 1
                val keys = response.listStory.map {
                    EntityDirection(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                storyDb.remoteKeysDao().addAll(keys)
                response.listStory.forEach { story ->
                    val item = ListStory(
                        story.photoUrl,
                        story.createdAt,
                        story.name,
                        story.description,
                        story.id,
                        story.lat,
                        story.lon
                    )
                    storyDb.storyDao().addStory(item)
                }
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReach)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyFirstItem(state: PagingState<Int, ListStory>): EntityDirection? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { data ->
            storyDb.remoteKeysDao().getRemoteId(data.id)
        }
    }

    private suspend fun getRemoteKeyLastItem(state: PagingState<Int, ListStory>): EntityDirection? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { data ->
            storyDb.remoteKeysDao().getRemoteId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListStory>): EntityDirection? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyDb.remoteKeysDao().getRemoteId(id)
            }
        }
    }

    companion object {
        const val initialPageIndex = 1
    }
}