package com.example.submissionappstory.di

import android.content.Context
import com.example.submissionappstory.data.local.pagedir.TokenPreferences
import com.example.submissionappstory.data.local.repository.MainRepository
import com.example.submissionappstory.data.local.repository.MapsRepository
import com.example.submissionappstory.data.local.repository.StoryRepository
import com.example.submissionappstory.data.local.room.StoryDb
import com.example.submissionappstory.data.remote.retrofit.ApiConfig

object Injection {

    fun storyRepo(tokenPref: TokenPreferences, context: Context): MainRepository {
        val storyDb = StoryDb.getInstance(context)
        val apiService = ApiConfig.getApiService()
        return MainRepository(storyDb, apiService, tokenPref)
    }

    fun uploadRepo() : StoryRepository {
        return StoryRepository()
    }

    fun mapsRepo(): MapsRepository {
        return MapsRepository()
    }
}