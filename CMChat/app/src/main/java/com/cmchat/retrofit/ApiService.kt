package com.cmchat.retrofit

import com.cmchat.model.LoginRequest
import com.cmchat.model.User
import com.cmchat.retrofit.model.FriendsResponse
import com.cmchat.retrofit.model.InfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {

    @POST("login/")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ) : Response<User>

    @GET("friends/")
    suspend fun getFriends(
        @Query("id") id : Int,
        @Query("status") status : Int
    ) : Response<FriendsResponse>

    @POST("friend/request")
    suspend fun addFriend(
        @Query("id") id : Int,
        @Query("friendUsername") friendUsername : String
    ) : Response<InfoResponse>

    @PUT("signup/")
    suspend fun signup(
        @Body user : User
    ) : Response<InfoResponse>

    @POST("edit/")
    suspend fun edit(
        @Body user : User
    ) : Response<User>

}
