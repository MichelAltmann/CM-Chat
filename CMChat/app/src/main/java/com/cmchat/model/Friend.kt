package com.cmchat.model

import java.util.Date

class Friend(
    val backgroundImage: ByteArray?,
    val bio: String,
    val birthday: Date,
    val createdDate: String,
    val id: Int,
    val profileImage: ByteArray?,
    val username: String
)
