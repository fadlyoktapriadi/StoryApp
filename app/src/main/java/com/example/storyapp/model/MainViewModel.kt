package com.example.storyapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.preferences.UserModel
import com.example.storyapp.repository.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getStories(token: String) = repository.getStories(token)

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}

