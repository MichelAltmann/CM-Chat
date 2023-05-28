package com.cmchat.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cmchat.cmchat.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy {
        FriendsAdapter()
    }

    private val viewModel by viewModel<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userList.adapter = adapter

        friendsObserver()

        viewModel.getFriends()

    }

    private fun friendsObserver() {
        viewModel.friendsResponse.observe(this) {
            it?.let {
                adapter.update(it)
            }
        }
    }
}