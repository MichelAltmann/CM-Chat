package com.cmchat

import android.app.Application
import com.cmchat.socket.SocketHandler
import io.socket.client.Socket

class Application : Application() {

    private lateinit var socket : Socket

    override fun onCreate() {
        super.onCreate()
        SocketHandler.setSocket()
        socket = SocketHandler.getSocket()
        socket.connect()
    }

    fun getSocket() : Socket {
        return socket
    }

}