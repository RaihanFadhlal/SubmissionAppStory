package com.example.submissionappstory.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.example.submissionappstory.data.local.repository.MainRepository
import com.example.submissionappstory.data.remote.apiresponse.ListStory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepo: MainRepository
): ViewModel() {

    val loading = MutableLiveData<Boolean>()

    fun getStory(): LiveData<PagingData<ListStory>> =
        mainRepo.getStory()
}