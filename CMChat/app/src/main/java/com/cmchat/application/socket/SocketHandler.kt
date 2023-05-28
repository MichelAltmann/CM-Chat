package com.cmchat.application.socket

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketHandler {

    lateinit var mSocket : Socket

    @Synchronized
    fun setSocket(){
        try {
            mSocket = IO.socket("http://192.168.31.76:8081")
        } catch (e: URISyntaxException) {

        }
    }

    @Synchronized
    fun getSocket() : Socket{
        return mSocket
    }

}