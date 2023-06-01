package com.cmchat.ui.main.profile.editprofile

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmchat.application.Application
import com.cmchat.model.User
import com.cmchat.retrofit.NetworkResponse
import com.cmchat.retrofit.RepositoryInterface
import com.cmchat.ui.main.MainActivity
import kotlinx.coroutines.launch
import java.lang.Exception

class EditProfileViewModel(private val repository: RepositoryInterface, private val application: Application) : ViewModel() {

    private val _userResponse = MutableLiveData<User>()
    val userResponse : LiveData<User> = _userResponse

    private val _error = MutableLiveData<Exception>()
    val error : LiveData<Exception> = _error

    fun edit(user: User) = viewModelScope.launch {
        when(val response = repository.edit(user)){
            is NetworkResponse.Success -> {
                _userResponse.value = response.data!!
                application.setUser(response.data)
                Log.i(TAG, "edit: " + application.getUser().username)
            }
            is NetworkResponse.Failed -> {
                _error.value = response.error!!
            }
        }
    }

}