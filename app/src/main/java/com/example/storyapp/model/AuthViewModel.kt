package com.example.storyapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.preferences.UserModel
import com.example.storyapp.repository.StoryRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: StoryRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        repository.authRegister(name, email, password)

    fun login(email: String, password: String) = repository.authLogin(email, password)
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}

