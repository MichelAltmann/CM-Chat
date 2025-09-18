package com.cmchat.application.socket

import androidx.fragment.app.Fragment
import com.cmchat.application.Application
import com.cmchat.cmchat.R
import com.cmchat.model.Message
import com.cmchat.notifications.MessageNotification
import com.google.gson.Gson
import org.json.JSONObject


class SocketController(private val application: Application){

    private val socket = application.getSocket()
    private val user = application.getUser()
    lateinit var newMessage: (Message) -> Unit

    @Synchronized
    fun newMessages() {
        socket.on("newMessage" + user.userId) { args ->
            if (args[0] != null) {
                val messageJson = args[0] as JSONObject
                val gson = Gson()
                val message = gson.fromJson(messageJson.toString(), Message::class.java)


                if (application.getCurrentFragment() == R.id.ChatFragment) {
                    newMessage.invoke(message)
                } else if (message.senderId != user.userId){
                    MessageNotification.sendNotification(application.applicationContext, message)
                }

            }
        }
    }



}