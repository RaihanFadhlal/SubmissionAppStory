package com.example.submissionappstory.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.submissionappstory.data.local.repository.AccountRepository
import com.example.submissionappstory.data.remote.apiresponse.LoginResult
import com.example.submissionappstory.data.remote.apiresponse.RegisResponse
import com.example.submissionappstory.ui.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogResViewModel @Inject constructor(
    private val accountRepo: AccountRepository,
) : ViewModel() {

    val regMessage: LiveData<Event<String>>
        get() = accountRepo.regMessage

    val logMessage: LiveData<Event<String>>
        get() = accountRepo.logMessage

    val loginUser: LiveData<LoginResult> = accountRepo.loginUser
    val registerUser: LiveData<RegisResponse> = accountRepo.registerUser
    val isLoading: LiveData<Boolean> = accountRepo.isLoading

    fun login(email: String, password: String) =
        accountRepo.login(email, password)

    fun register(username: String, email: String, password: String) =
        accountRepo.register(username, email, password)
}