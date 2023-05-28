package com.cmchat.retrofit

import com.cmchat.model.LoginRequest
import com.cmchat.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("login/")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ) : Response<User>

    @GET("friends/{id}")
    suspend fun getFriends(
        @Path("id") id : Int
    ) : Response<FriendsResponse>

}
