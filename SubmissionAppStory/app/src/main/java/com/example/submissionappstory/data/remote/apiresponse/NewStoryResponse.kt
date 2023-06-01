package com.example.submissionappstory.data.remote.apiresponse

import com.google.gson.annotations.SerializedName

class NewStoryResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)