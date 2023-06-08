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
import com.cmchat.retrofit.model.ImageResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

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

    private val _bgResponse = MutableLiveData<ImageResponse>()
    val bgResponse : LiveData<ImageResponse> = _bgResponse
    private val _profileResponse = MutableLiveData<ImageResponse>()
    val profileResponse : LiveData<ImageResponse> = _profileResponse

    private val _imageError = MutableLiveData<Exception>()
    val imageError : LiveData<Exception> = _imageError

    fun uploadImage(byteArray : ByteArray, profileOrBg : Int) = viewModelScope.launch {
        val requestBody = byteArray.toRequestBody("image/jpeg".toMediaType())
        when(val response = repository.uploadImage(MultipartBody.Part.createFormData("image", "image.jpg", requestBody))){
            is NetworkResponse.Success -> {
                if (profileOrBg == 1) {
                    _bgResponse.value = response.data!!
                    Log.i(TAG, "uploadImage: " + response.data)
                } else {
                    _profileResponse.value = response.data!!
                }
            }
            is NetworkResponse.Failed -> {
                _error.value = response.error!!
            }
        }
    }

}