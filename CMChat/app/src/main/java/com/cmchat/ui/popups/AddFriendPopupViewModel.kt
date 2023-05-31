package com.cmchat.ui.popups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmchat.application.Application
import com.cmchat.retrofit.NetworkResponse
import com.cmchat.retrofit.RepositoryInterface
import com.cmchat.retrofit.model.InfoResponse
import kotlinx.coroutines.launch
import java.lang.Exception

class AddFriendPopupViewModel(private val repository : RepositoryInterface, private val application: Application) : ViewModel() {

    private val _friendAddResponse = MutableLiveData<InfoResponse>()
    val friendAddResponse : LiveData<InfoResponse> = _friendAddResponse

    private val _error = MutableLiveData<Exception>()
    val error : LiveData<Exception> = _error

    var username = ""

    fun addFriend() = viewModelScope.launch {
        when(val response = repository.addFriend(application.getUser().id, username)){
            is NetworkResponse.Success -> {
                _friendAddResponse.value = response.data!!
            }
            is NetworkResponse.Failed -> {
                _error.value = response.error!!
            }
        }
    }
}