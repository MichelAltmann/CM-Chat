package com.cmchat.application.socket

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketHandler {

    lateinit var mSocket : Socket

    @Synchronized
    fun setSocket(){
        try {
            mSocket = IO.socket("http://10.147.17.129:8081")
        } catch (e: URISyntaxException) {

        }
    }

    @Synchronized
    fun getSocket() : Socket{
        return mSocket
    }

}