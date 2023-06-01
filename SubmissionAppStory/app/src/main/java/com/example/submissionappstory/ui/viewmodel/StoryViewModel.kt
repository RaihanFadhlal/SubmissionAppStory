package com.example.submissionappstory.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.submissionappstory.data.local.repository.StoryRepository
import com.example.submissionappstory.data.remote.apiresponse.ListStory
import com.example.submissionappstory.data.remote.apiresponse.NewStoryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val storyRepo: StoryRepository
) : ViewModel() {

    val story: LiveData<List<ListStory>> = storyRepo.story
    val storyResponse: LiveData<NewStoryResponse> = storyRepo.storyResponse
    val isEnabled: LiveData<Boolean> = storyRepo.isEnabled

    fun uploadStory(
        token: String,
        image: MultipartBody.Part,
        desc: RequestBody,
        lat: Double? = null,
        lon: Double? = null
    ) = storyRepo.uploadStory(token, image, desc, lat, lon)
}