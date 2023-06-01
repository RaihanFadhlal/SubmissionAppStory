package com.example.submissionappstory.data.remote.retrofit

import com.example.submissionappstory.data.remote.apiresponse.DetStoryResponse
import com.example.submissionappstory.data.remote.apiresponse.LoginResponse
import com.example.submissionappstory.data.remote.apiresponse.NewStoryResponse
import com.example.submissionappstory.data.remote.apiresponse.RegisResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @POST("register")
    @FormUrlEncoded
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisResponse>

    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") token: String,
        @Query("size") size: Int,
        @Query("page") page: Int
    ): DetStoryResponse

    @GET("stories")
    fun getStoriesLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int
    ): Call<DetStoryResponse>

    @Multipart
    @POST("stories")
    fun uploadStoryLocation(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): Call<NewStoryResponse>
}