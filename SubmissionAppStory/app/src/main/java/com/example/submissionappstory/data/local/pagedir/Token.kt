package com.example.submissionappstory.data.local.pagedir

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class Token(private val tokenPref: TokenPreferences) : ViewModel() {

    fun setToken(token: String) {
        viewModelScope.launch {
            tokenPref.setToken(token)
        }
    }

    fun getToken(): LiveData<String> = tokenPref.getToken().asLiveData()

    fun deleteToken() {
        viewModelScope.launch {
            tokenPref.deleteToken()
        }
    }
}