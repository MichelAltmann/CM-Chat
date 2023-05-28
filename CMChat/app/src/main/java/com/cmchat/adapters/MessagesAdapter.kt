package com.cmchat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmchat.application.socket.SocketHandler
import com.cmchat.model.Message
import com.cmchat.cmchat.R
import com.cmchat.cmchat.databinding.MessageItemBinding

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    private val messages : ArrayList<Message> = arrayListOf()

    inner class ViewHolder(private val binding: MessageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message){
            if (message.image == null){
                if (message.senderId == SocketHandler.getSocket().id()){
                    binding.userMessage.visibility = View.VISIBLE
                    binding.userMessageText.text = message.text
                    binding.friendMessage.visibility = View.GONE
                } else {
                    binding.friendMessage.visibility = View.VISIBLE
                    binding.friendMessageText.text = message.text
                    binding.userMessage.visibility = View.GONE
                }
            } else {
                if (message.senderId == SocketHandler.getSocket().id()){
                    binding.userMessage.visibility = View.VISIBLE
                    Glide.with(binding.userMessageImage).load(message.image).into(binding.userMessageImage)
                    binding.friendMessage.visibility = View.GONE
                } else {
                    binding.friendMessage.visibility = View.VISIBLE
                    Glide.with(binding.friendMessageImage).load(message.image).into(binding.friendMessageImage)
                    binding.userMessage.visibility = View.GONE
                }
            }
            loadStatus(message, binding)
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

    private fun loadStatus(message : Message, binding: MessageItemBinding) {
        when(message.status){
            "sending" -> Glide.with(binding.userMessageStatus).load(R.drawable.ic_sending).into(binding.userMessageStatus)
            "sent" -> Glide.with(binding.userMessageStatus).load(R.drawable.ic_sent).into(binding.userMessageStatus)
        }


    }

    fun update(messages : ArrayList<Message>){
        this.messages.clear()
        this.messages.addAll(messages)
        notifyItemChanged(messages.size - 1)
    }

}