package com.cmchat.model

class Message(
    val senderId : String,
    val receiverId : Int,
    val text : String,
    val image : ByteArray?,
    val status : String
)