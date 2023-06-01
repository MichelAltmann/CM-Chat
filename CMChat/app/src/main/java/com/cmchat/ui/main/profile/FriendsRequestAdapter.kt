package com.cmchat.ui.main.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmchat.cmchat.R
import com.cmchat.cmchat.databinding.FriendRequestItemBinding
import com.cmchat.model.Friend
import java.text.SimpleDateFormat
import java.util.Locale

class FriendsRequestAdapter : RecyclerView.Adapter<FriendsRequestAdapter.ViewHolder>() {

    private val friendsRequest : ArrayList<Friend> = arrayListOf()
    lateinit var friendRequestClick : (Int) -> Unit
    lateinit var acceptRequestClick : (Int) -> Unit
    lateinit var refuseRequestClick : (Int) -> Unit

    inner class ViewHolder(private val binding : FriendRequestItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(friend : Friend) {
            val formatter = SimpleDateFormat("dd 'of' MMM yyyy", Locale.getDefault())
            binding.friendUserName.text = friend.username
            Glide.with(binding.friendUserImage).load(friend.profileImage).placeholder(R.drawable.ic_user).into(binding.friendUserImage)
            binding.friendUserBirthdate.text = formatter.format(friend.birthday)
            itemView.setOnClickListener {
                friendRequestClick.invoke(friend.id)
            }

            binding.friendRequestAcceptBtn.setOnClickListener {
                acceptRequestClick.invoke(friend.id)
            }
            binding.friendRequestRefuseBtn.setOnClickListener {
                refuseRequestClick.invoke(friend.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(binding = FriendRequestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return friendsRequest.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(friendsRequest[position])
    }

    fun update(friendsRequest : ArrayList<Friend>){
        this.friendsRequest.clear()
        this.friendsRequest.addAll(friendsRequest)
        notifyDataSetChanged()
    }

}