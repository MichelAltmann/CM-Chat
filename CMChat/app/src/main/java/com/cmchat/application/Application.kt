package com.cmchat.application

import android.app.Application
import com.cmchat.application.socket.SocketHandler
import com.cmchat.model.User
import io.socket.client.Socket
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import kotlin.math.log

class Application : Application() {

    private lateinit var socket : Socket

    private lateinit var loggedUser : User

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

    fun setUser(user : User) {
        loggedUser = user
    }

    fun getUser() : User{
        return loggedUser
    }

    fun getSocket() : Socket {
        return socket
    }

}