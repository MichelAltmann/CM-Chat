package com.cmchat.ui.main.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmchat.application.Application
import com.cmchat.application.socket.SocketController
import com.cmchat.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(private val  application: Application) : ViewModel() {

    private val _messageResponse = MutableLiveData<Message>()
    val messageResponse : LiveData<Message> = _messageResponse

    fun newMessage() = viewModelScope.launch{
        val socketController = application.getController()
        socketController.newMessage = {
            viewModelScope.launch {
                _messageResponse.value = it
            }
        }
    }


}