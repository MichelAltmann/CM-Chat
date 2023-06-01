package com.cmchat.ui.login

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmchat.application.Application
import com.cmchat.model.LoginRequest
import com.cmchat.model.User
import com.cmchat.retrofit.NetworkResponse
import com.cmchat.retrofit.RepositoryInterface
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel(
    private val repository: RepositoryInterface,
    private val application: Application
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _error = MutableLiveData<Exception>()
    val error : LiveData<Exception> = _error

    fun authenticateUser(loginRequest: LoginRequest) = viewModelScope.launch {
        when (val response = repository.login(loginRequest = loginRequest)) {
            is NetworkResponse.Success -> {
                _user.value = response.data
                application.connect()
                application.setUser(response.data)
                application.setController()
            }
            is NetworkResponse.Failed -> {
                _error.value = response.error
                Log.i(TAG, "authenticateUser: " + error.value.toString())
            }
        }
    }

}