package com.cmchat.ui.main

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.bumptech.glide.Glide
import com.cmchat.application.Application
import com.cmchat.cmchat.R
import com.cmchat.cmchat.databinding.ActivityMainBinding
import com.cmchat.model.User
import com.cmchat.ui.main.chat.ChatFragment
import com.cmchat.util.ImageHandler

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val application by lazy {
        applicationContext as Application
    }

    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        user = application.getUser()

        updateUser()



        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.profileCard.setOnClickListener {
            navController.navigate(R.id.action_HomeFragment_to_ProfileFragment)
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id){
                R.id.ProfileFragment -> {
                    binding.profileCard.visibility = View.GONE
                }
                R.id.HomeFragment -> {
                    updateUser()
                    binding.profileCard.visibility = View.VISIBLE
                }
                R.id.ChatFragment -> {
                    binding.profileCard.visibility = View.GONE
                }
            }
            application.setCurrentFragment(destination.id)
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
    private fun updateUser(){
        user = application.getUser()
        Glide.with(applicationContext).load(ImageHandler.IMAGE_GETTER_URL+user.profileImage).placeholder(R.drawable.ic_user).into(binding.profileIcon)
    }

}

