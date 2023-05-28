package com.cmchat.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmchat.application.Application
import com.cmchat.retrofit.FriendsResponse
import com.cmchat.retrofit.NetworkResponse
import com.cmchat.retrofit.RepositoryInterface
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(private val repository: RepositoryInterface, private val application: Application) : ViewModel() {

    private val user = application.getUser()

    private val _friendsResponse = MutableLiveData<FriendsResponse>()
    val friendsResponse : LiveData<FriendsResponse> = _friendsResponse
    private val _error = MutableLiveData<Exception>()
    val error : LiveData<Exception> = _error

    fun getFriends() = viewModelScope.launch{
        when (val response = repository.getFriends(user.id)) {
            is NetworkResponse.Success -> {
                _friendsResponse.value = response.data!!
            }
            is NetworkResponse.Failed -> {
                _error.value = response.error
            }
        }
    }

}