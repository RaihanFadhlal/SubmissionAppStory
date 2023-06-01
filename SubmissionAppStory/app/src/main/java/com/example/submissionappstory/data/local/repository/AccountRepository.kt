package com.example.submissionappstory.data.local.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.submissionappstory.data.remote.apiresponse.LoginResponse
import com.example.submissionappstory.data.remote.apiresponse.LoginResult
import com.example.submissionappstory.data.remote.apiresponse.RegisResponse
import com.example.submissionappstory.data.remote.retrofit.ApiConfig
import com.example.submissionappstory.ui.util.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountRepository {
    private val login = MutableLiveData<LoginResponse>()

    private val _registerUser = MutableLiveData<RegisResponse>()
    val registerUser: LiveData<RegisResponse> = _registerUser

    private val _loginUser = MutableLiveData<LoginResult>()
    val loginUser: LiveData<LoginResult> = _loginUser

    private val _isEnabled = MutableLiveData<Boolean>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _regMessage = MutableLiveData<Event<String>>()
    val regMessage: LiveData<Event<String>>
        get() = _regMessage

    private val _logMessage = MutableLiveData<Event<String>>()
    val logMessage: LiveData<Event<String>>
        get() = _logMessage

    fun register(name: String, email: String, password: String): LiveData<RegisResponse> {
        _isEnabled.value = false
        _isLoading.value = true

        ApiConfig.getApiService().register(name, email, password)
            .enqueue(object : Callback<RegisResponse> {
                override fun onResponse(
                    call: Call<RegisResponse>,
                    response: Response<RegisResponse>,
                ) {
                    _isEnabled.value = true
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _registerUser.postValue(response.body())
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                        _regMessage.value = Event("")
                    }
                }

                override fun onFailure(call: Call<RegisResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })
        return _registerUser
    }

    fun login(email: String, password: String): LiveData<LoginResponse> {
        _isEnabled.value = false
        _isLoading.value = true

        Log.e(TAG, "The Result is $email")

        ApiConfig.getApiService().login(email, password)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>,
                ) {
                    _isEnabled.value = true
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        response.body().let { login ->
                            login?.loginResult?.let {
                                _loginUser.value = LoginResult(it.name, it.userId, it.token)
                            }
                        }
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                        _logMessage.value = Event("")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })
        return login
    }

    companion object {
        private const val TAG = "AuthRepository"
    }
}
