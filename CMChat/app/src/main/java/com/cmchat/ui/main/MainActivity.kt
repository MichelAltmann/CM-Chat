package com.cmchat.ui.main

import android.content.ContentValues.TAG
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.Resource
import com.cmchat.application.Application
import com.cmchat.cmchat.R
import com.cmchat.cmchat.databinding.ActivityMainBinding
import com.cmchat.model.User
import org.koin.core.component.getScopeId

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
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
    private fun updateUser(){
        user = application.getUser()
        Glide.with(applicationContext).load(user.profileImage).placeholder(R.drawable.ic_user).into(binding.profileIcon)
    }

}

