package com.example.submissionappstory.data.local.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.submissionappstory.data.remote.apiresponse.ListStory
import com.example.submissionappstory.data.remote.apiresponse.NewStoryResponse
import com.example.submissionappstory.data.remote.retrofit.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository {

    private val _story = MutableLiveData<List<ListStory>>()
    val story: LiveData<List<ListStory>> = _story

    private val _storyResponse = MutableLiveData<NewStoryResponse>()
    val storyResponse: LiveData<NewStoryResponse> = _storyResponse

    private val _isEnabled = MutableLiveData<Boolean>()
    val isEnabled: LiveData<Boolean> = _isEnabled

    fun uploadStory(
        token: String,
        image: MultipartBody.Part,
        desc: RequestBody,
        lat: Double?,
        lon: Double?,
    ): LiveData<NewStoryResponse> {

        _isEnabled.value = false
        ApiConfig.getApiService().uploadStoryLocation(token, image, desc, lat, lon)
            .enqueue(object : Callback<NewStoryResponse> {
                override fun onResponse(
                    call: Call<NewStoryResponse>,
                    response: Response<NewStoryResponse>
                ) {
                    _isEnabled.value = true
                    if (response.isSuccessful) {
                        _storyResponse.postValue(response.body())
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<NewStoryResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })
        return _storyResponse
    }

    companion object {
        private const val TAG = "UploadRepo"
    }
}