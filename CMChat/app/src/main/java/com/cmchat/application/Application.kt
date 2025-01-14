package com.cmchat.application

import android.app.Application
import androidx.fragment.app.Fragment
import com.cmchat.application.socket.SocketController
import com.cmchat.application.socket.SocketHandler
import com.cmchat.model.User
import com.cmchat.notifications.MessageNotification
import com.cmchat.webrtc.SocketRepository
import io.socket.client.Socket
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import kotlin.math.log

class Application : Application() {

    private lateinit var socket : Socket

    private lateinit var loggedUser : User

    private lateinit var socketController: SocketController

    private lateinit var socketRepository: SocketRepository

    private var currentFragment = 0
    override fun onCreate() {
        super.onCreate()

        MessageNotification.createNotificationChannel(applicationContext)

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

    fun setController(){
        socketController = SocketController(this)
        socketController.newMessages()
    }

    fun getController() : SocketController {
        return socketController
    }

    fun setSocketRepository(socketRepository: SocketRepository) {
        this.socketRepository = socketRepository
    }

    fun getSocketRepository() : SocketRepository {
        return socketRepository
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

    fun setCurrentFragment(fragment : Int) {
        currentFragment = fragment
    }

    fun getCurrentFragment() : Int {
        return currentFragment
    }



}