package com.cmchat.webrtc.util

import com.cmchat.webrtc.models.MessageModel

interface NewMessageInterface {
    fun onNewMessage(message:MessageModel)
}