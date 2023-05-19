package com.cmchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cmchat.adapters.MessagesAdapter
import com.cmchat.cmchat.R
import com.cmchat.cmchat.databinding.ActivityMainBinding
import com.cmchat.socket.SocketHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.socket.client.IO
import org.json.JSONObject

private var _binding : ActivityMainBinding? = null
private val binding get() = _binding!!
private val messagesAdapter by lazy {
    MessagesAdapter()
}
private val messages : ArrayList<Message> = arrayListOf()

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SocketHandler.setSocket()

        val mSocket = SocketHandler.getSocket()

        mSocket.connect()

        binding.messagesRecycler.adapter = messagesAdapter

        binding.fabSendMessage.setOnClickListener {
            val message = Message( binding.textInput.text.toString(), mSocket.id())
            if (message.text.isNotEmpty()){
                val gson = Gson()
                val messageJson = gson.toJson(message)
                mSocket.emit("newMessage",messageJson)
                binding.textInput.setText("")
            }
        }

        mSocket.on("newMessage") { args ->
            if (args[0] != null){
                val messageJson = args[0] as JSONObject
                val gson = Gson()
                val message = gson.fromJson(messageJson.toString(), Message::class.java)
                runOnUiThread {
                    messages.add(message)
                    messagesAdapter.update(messages)
                    binding.messagesRecycler.scrollToPosition(messages.size - 1)
                }
            }
        }

    }
}