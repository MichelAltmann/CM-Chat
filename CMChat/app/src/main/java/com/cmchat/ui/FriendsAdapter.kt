package com.cmchat.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmchat.cmchat.databinding.UserItemBinding
import com.cmchat.model.Friend

class FriendsAdapter : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    private val friends : ArrayList<Friend> = arrayListOf()

    inner class ViewHolder(private val binding : UserItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(friend : Friend) {
            binding.userName.text = friend.username
            binding.userLastMessage.text = friend.id.toString()
            binding.userLastMessageTime.text = "00:00"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(friends[position])
    }

    fun update(friends : ArrayList<Friend>){
        this.friends.clear()
        this.friends.addAll(friends)
        notifyDataSetChanged()
    }

}