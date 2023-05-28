package com.cmchat.model

import java.util.Date

class User(
    val id : Int,
    val email : String,
    val username : String,
    val birthday : Date,
    val profileImage : ByteArray?,
    val backgroundImage : ByteArray?,
    val bio : String?,
    val createdDate : Date,
    val deleted : Int,
    val isSuspended : Int
)