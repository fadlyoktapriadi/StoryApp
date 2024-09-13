package com.example.storyapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.preferences.UserModel
import com.example.storyapp.repository.StoryRepository

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getStoriesWithLocation(token: String) = repository.getStoriesWithLocation(token)

}

