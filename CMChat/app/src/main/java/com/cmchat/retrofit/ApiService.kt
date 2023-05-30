package com.cmchat.retrofit

import com.cmchat.model.LoginRequest
import com.cmchat.model.User
import com.cmchat.retrofit.model.FriendsResponse
import com.cmchat.retrofit.model.SignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @PUT("signup/")
    suspend fun signup(
        @Body user : User
    ) : Response<SignupResponse>

}
