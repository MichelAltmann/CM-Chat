package com.cmchat.application

import android.app.Application
import com.cmchat.socket.SocketHandler
import io.socket.client.Socket
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class Application : Application() {

    private lateinit var socket : Socket

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()

            androidContext(this@Application)

            modules(appModules)
        }
    }

    fun connect() {
        SocketHandler.setSocket()
        socket = SocketHandler.getSocket()
        socket.connect()
    }

    fun getSocket() : Socket {
        return socket
    }

}