package com.cmchat.model

import java.util.Date

class User(
    val userId : Int,
    val email : String?,
    val nickname : String?,
    val username : String,
    val password : String?,
    val birthday : Date?,
    val profileImage : String?,
    val backgroundImage : String?,
    val biography : String?,
    val createdDate : Date?,
    val deleted : Boolean,
)