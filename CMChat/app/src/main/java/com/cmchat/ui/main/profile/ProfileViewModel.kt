package com.cmchat.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmchat.application.Application
import com.cmchat.model.User
import com.cmchat.retrofit.NetworkResponse
import com.cmchat.retrofit.RepositoryInterface
import com.cmchat.retrofit.model.FriendsResponse
import com.cmchat.retrofit.model.InfoResponse
import kotlinx.coroutines.launch
import java.lang.Exception

class ProfileViewModel(private val repository: RepositoryInterface, private val application: Application) : ViewModel() {

    private val user = application.getUser()

    private val _friendsRequestResponse = MutableLiveData<FriendsResponse>()
    val friendsRequestResponse : LiveData<FriendsResponse> = _friendsRequestResponse
    private val _error = MutableLiveData<Exception>()
    val error : LiveData<Exception> = _error

    fun getFriends() = viewModelScope.launch{
        when (val response = repository.getFriends(user.id, 0)) {
            is NetworkResponse.Success -> {
                _friendsRequestResponse.value = response.data!!
            }
            is NetworkResponse.Failed -> {
                _error.value = response.error
            }
        }
    }

    private val _requestResponse = MutableLiveData<InfoResponse>()
    val requestResponse : LiveData<InfoResponse> = _requestResponse

    private val _requestError = MutableLiveData<Exception>()
    val requestError : LiveData<Exception> = _requestError

    fun acceptRequest(friendId : Int) = viewModelScope.launch {
        when (val response = repository.acceptFriend(user.id, friendId)) {
            is NetworkResponse.Success -> {
                _requestResponse.value = response.data!!
            }
            is NetworkResponse.Failed -> {
                _requestError.value = response.error
            }
        }
    }

    fun refuseRequest(friendId: Int) = viewModelScope.launch {
        when (val response = repository.refuseFriend(user.id, friendId)) {
            is NetworkResponse.Success -> {
                _requestResponse.value = response.data!!
            }
            is NetworkResponse.Failed -> {
                _requestError.value = response.error
            }
        }
    }

    fun getUser() : User {
        return user
    }
}