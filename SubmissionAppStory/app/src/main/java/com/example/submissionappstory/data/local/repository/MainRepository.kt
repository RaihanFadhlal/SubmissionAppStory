package com.example.submissionappstory.data.local.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.submissionappstory.data.local.pagedir.PageMediator
import com.example.submissionappstory.data.local.pagedir.TokenPreferences
import com.example.submissionappstory.data.local.room.StoryDb
import com.example.submissionappstory.data.remote.apiresponse.ListStory
import com.example.submissionappstory.data.remote.retrofit.ApiService
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val storyDb: StoryDb,
    private val apiService: ApiService,
    val tokenPref: TokenPreferences
) {

    fun getStory(): LiveData<PagingData<ListStory>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = PageMediator(storyDb, apiService, tokenPref),
            pagingSourceFactory = {
                storyDb.storyDao().getStory()
            }
        ).liveData
    }
}