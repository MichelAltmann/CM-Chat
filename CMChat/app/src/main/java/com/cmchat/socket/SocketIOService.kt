package com.cmchat.socket

import android.database.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.net.Socket

interface SocketIOService {

    @GET("/")
    fun connect(): Observable<Socket>

    @POST("/sendMessage")
    fun sendMessage(@Body message : String): Observable<Response<String>>

}