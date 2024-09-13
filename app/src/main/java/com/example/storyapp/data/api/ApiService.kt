package com.example.storyapp.data.api

import com.example.storyapp.data.response.ResponseFileUpload
import com.example.storyapp.data.response.ResponseLogin
import com.example.storyapp.data.response.ResponseRegister
import com.example.storyapp.data.response.ResponseStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): ResponseRegister

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): ResponseLogin

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
    ): ResponseStory

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): ResponseFileUpload

}