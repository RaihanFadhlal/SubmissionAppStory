package com.example.submissionappstory.datatest

import com.example.submissionappstory.data.remote.apiresponse.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object DummyData {

    fun loginResult(): LoginResponse {
        return LoginResponse(
            LoginResult(
                userId = "user-geIIL3Qju4jJJoC_",
                name = "loginTest",
                token = "niwndiwniownfesiofnoQ18iLCJpYXQiOjE2ODM1MzIfneskfniif3iofikeSrSnb_NLa7nBUa-U0FkDtU"
            ),
            false, "success"
        )
    }

    fun register(): RegisResponse {
        return RegisResponse(
            error = false,
            message = "success"
        )
    }

    fun dummyStory(): List<ListStory> {
        val data: MutableList<ListStory> = arrayListOf()

        for (i in 0..200) {
            val list = ListStory(
                "link",
                "29-5-2023",
                "raihan",
                "testStory",
                "$i",
                40.7143528,
                -74.0059731
            )
            data.add(list)
        }
        return data
    }

    val dummyStoryNull = emptyList<ListStory>()

    fun dummyDesc(): RequestBody {
        val text = "testDescription"
        return text.toRequestBody()
    }

    fun dummyImg(): MultipartBody.Part {
        val text = "testImage"
        return MultipartBody.Part.create(text.toRequestBody())
    }


    fun newStory(): NewStoryResponse {
        return NewStoryResponse(
            error = false,
            message = "success"
        )
    }
}