package com.cmchat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmchat.Message
import com.cmchat.cmchat.databinding.MessageItemBinding
import com.cmchat.socket.SocketHandler

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    private val messages : ArrayList<Message> = arrayListOf()

    inner class ViewHolder(private val binding: MessageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message){
            if (message.senderId == SocketHandler.getSocket().id()){
                binding.userMessage.visibility = View.VISIBLE
                binding.userMessageText.text = message.text
                binding.friendMessage.visibility = View.GONE
            } else {
                binding.friendMessage.visibility = View.VISIBLE
                binding.friendMessageText.text = message.text
                binding.userMessage.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    fun update(messages : ArrayList<Message>){
        this.messages.clear()
        this.messages.addAll(messages)
        notifyItemChanged(messages.size - 1)
    }

}