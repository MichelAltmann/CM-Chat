package com.cmchat.socket

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketHandler {

    lateinit var mSocket : Socket

    @Synchronized
    fun setSocket(){
        try {
            mSocket = IO.socket("http://10.0.2.2:8081")
        } catch (e: URISyntaxException) {

        }
    }

    @Synchronized
    fun getSocket() : Socket{
        return mSocket
    }

}