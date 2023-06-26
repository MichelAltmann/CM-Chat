package com.cmchat.ui.main.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmchat.cmchat.databinding.UserItemBinding
import com.cmchat.model.Friend
import com.cmchat.util.ImageHandler

class FriendsAdapter : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    private val friends : ArrayList<Friend> = arrayListOf()
    lateinit var friendClick : (Friend) -> Unit

    inner class ViewHolder(private val binding : UserItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(friend : Friend) {
            binding.userName.text = friend.username
            binding.userLastMessage.text = friend.id.toString()
            binding.userLastMessageTime.text = "00:00"
            Glide.with(binding.userImage).load(ImageHandler.IMAGE_GETTER_URL+friend.profileImage).into(binding.userImage)
            itemView.setOnClickListener {
                friendClick.invoke(friend)
            }
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