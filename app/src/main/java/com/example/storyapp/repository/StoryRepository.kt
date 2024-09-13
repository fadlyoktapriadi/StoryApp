package com.example.storyapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.paging.StoryPagingSource
import com.example.storyapp.data.preferences.UserModel
import com.example.storyapp.data.preferences.UserPreference
import com.example.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun authRegister(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("${e.message}"))
        }
    }

    fun authLogin(email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("${e.message}"))
        }
    }

    fun getStoriesWithLocation(token: String): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesWithLocation(token)
            emit(Result.Success(response.listStory))
        } catch (e: Exception) {
            emit(Result.Error("${e.message}"))
        }
    }

    fun uploadStory(token: String, imgFile: File, desc: String) = liveData {
        emit(Result.Loading)
        val requestBody = desc.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imgFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imgFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.uploadStory(token, multipartBody, requestBody)
            emit(Result.Success(successResponse))
        } catch (e: Exception) {
            emit(Result.Error("${e.message}"))
        }
    }

    fun uploadStoryWithLocation(
        token: String,
        imgFile: File,
        desc: String,
        lat: Float,
        lon: Float
    ) = liveData {
        emit(Result.Loading)
        val requestBody = desc.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imgFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imgFile.name,
            requestImageFile
        )
        try {
            val successResponse =
                apiService.uploadStoryWithLocation(token, multipartBody, requestBody, lat, lon)
            emit(Result.Success(successResponse))
        } catch (e: Exception) {
            emit(Result.Error("${e.message}"))
        }
    }

    fun getStoryPaging(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
    }


    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference)
            }.also { instance = it }
    }
}