package com.cmchat.ui.login.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmchat.model.User
import com.cmchat.retrofit.NetworkResponse
import com.cmchat.retrofit.RepositoryInterface
import com.cmchat.retrofit.model.SignupResponse
import kotlinx.coroutines.launch
import java.lang.Exception

class SignupViewModel(private val repository: RepositoryInterface) : ViewModel() {

    private val _message = MutableLiveData<SignupResponse>()
    val message : LiveData<SignupResponse> = _message

    private val _error = MutableLiveData<Exception>()
    val error : LiveData<Exception> = _error

    fun signup(user: User) = viewModelScope.launch {
        when(val response = repository.signup(user)){
            is NetworkResponse.Success -> {
                _message.value = response.data!!
            }
            is NetworkResponse.Failed -> {
                _error.value = response.error!!
            }
        }
    }

}