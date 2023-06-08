package com.cmchat.model

import java.util.Date

class Friend(
    val backgroundImage: String?,
    val bio: String,
    val birthday: Date,
    val createdDate: String,
    val id: Int,
    val profileImage: String?,
    val username: String,
    val nickname: String,
    val status: Int // 0 - pending 1 - accepted 2 - refused
)
