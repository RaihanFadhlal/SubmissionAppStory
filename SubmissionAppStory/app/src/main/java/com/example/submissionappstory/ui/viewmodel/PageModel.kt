package com.example.submissionappstory.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.submissionappstory.data.local.repository.MainRepository
import com.example.submissionappstory.data.remote.apiresponse.ListStory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PageModel @Inject constructor(
    mainRepo: MainRepository
) : ViewModel() {

    val getStory: LiveData<PagingData<ListStory>> =
        mainRepo.getStory().cachedIn(viewModelScope)
}