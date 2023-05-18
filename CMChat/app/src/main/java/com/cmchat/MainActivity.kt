package com.cmchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cmchat.cmchat.R
import com.cmchat.cmchat.databinding.ActivityMainBinding
import com.cmchat.socket.SocketHandler
import io.socket.client.IO

private var _binding : ActivityMainBinding? = null
private val binding get() = _binding!!

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SocketHandler.setSocket()

        val mSocket = SocketHandler.getSocket()

        mSocket.connect()

        binding.fabSendMessage.setOnClickListener {
            val message = binding.textInput.text.toString()
            if (message.isNotEmpty()){
                mSocket.emit("newMessage",message)
                binding.textInput.setText("")
            }
        }

        mSocket.on("newMessage") { args ->
            if (args[0] != null){
                val message = args[0] as String
                runOnUiThread {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}