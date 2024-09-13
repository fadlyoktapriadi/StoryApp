package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.preferences.UserPreference
import com.example.storyapp.data.preferences.dataStore
import com.example.storyapp.repository.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val pref = UserPreference.getInstance(context.dataStore)
        return StoryRepository.getInstance(apiService, pref)
    }
}