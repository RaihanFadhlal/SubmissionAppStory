package com.example.submissionappstory.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.submissionappstory.data.local.repository.MapsRepository
import com.example.submissionappstory.data.remote.apiresponse.ListStory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val mapsRepo: MapsRepository
): ViewModel() {

    fun getStoryLocation(token: String): LiveData<List<ListStory>> =
        mapsRepo.getStoryLocation(token)

    fun getStory(): LiveData<List<ListStory>> = mapsRepo.getStory()
}