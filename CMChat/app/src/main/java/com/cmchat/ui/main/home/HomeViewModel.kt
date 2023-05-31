package com.cmchat.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmchat.application.Application
import com.cmchat.retrofit.model.FriendsResponse
import com.cmchat.retrofit.NetworkResponse
import com.cmchat.retrofit.RepositoryInterface
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeViewModel(private val repository: RepositoryInterface, private val application: Application) : ViewModel() {


    private val _friendsResponse = MutableLiveData<FriendsResponse>()
    val friendsResponse : LiveData<FriendsResponse> = _friendsResponse
    private val _error = MutableLiveData<Exception>()
    val error : LiveData<Exception> = _error

    fun getFriends() = viewModelScope.launch{
        val user = application.getUser()
        when (val response = repository.getFriends(user.id, 1)) {
            is NetworkResponse.Success -> {
                _friendsResponse.value = response.data!!
            }
            is NetworkResponse.Failed -> {
                _error.value = response.error
            }
        }
    }

}