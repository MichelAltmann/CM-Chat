package com.cmchat.retrofit

import com.cmchat.model.LoginRequest
import com.cmchat.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.POST

interface ApiService {

    @POST("login/")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ) : Response<User>

}
