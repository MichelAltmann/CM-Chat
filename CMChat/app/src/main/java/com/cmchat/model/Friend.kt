package com.cmchat.model

import java.io.Serializable
import java.util.Date

class Friend(
    val backgroundImage: String?,
    val bio: String,
    val birthday: Date,
    val createdDate: String,
    val userId: Int,
    val profileImage: String?,
    val username: String,
    val nickname: String,
    val status: Int // 0 - pending 1 - accepted 2 - refused
) : Serializable
