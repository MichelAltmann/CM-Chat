package com.cmchat.model

class Message(
    val senderId : Int,
    val receiverId : Int,
    val text : String,
    val image : ByteArray?,
    val status : String
)