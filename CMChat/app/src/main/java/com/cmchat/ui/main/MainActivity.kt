package com.cmchat.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.cmchat.application.Application
import com.cmchat.cmchat.R
import com.cmchat.cmchat.databinding.ActivityMainBinding
import com.cmchat.model.User
import com.cmchat.ui.main.chat.videocall.VideoCallActivity
import com.cmchat.webrtc.SocketRepository
import com.cmchat.webrtc.models.MessageModel
import com.cmchat.webrtc.util.NewMessageInterface

class MainActivity : AppCompatActivity(), NewMessageInterface {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val application by lazy {
        applicationContext as Application
    }
    private var socketRepository: SocketRepository? = null
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = application.getUser()

        permissionsRequest()

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            application.setCurrentFragment(destination.id)
        }

        init()
    }

    private fun init() {
        socketRepository = SocketRepository(this)
        socketRepository?.initSocket(user.username)
        application.setSocketRepository(socketRepository!!)
    }

    private fun permissionsRequest() {
        val permissionsList = arrayListOf<String>()
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) permissionsList.add(
            Manifest.permission.POST_NOTIFICATIONS
        )
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) permissionsList.add(
            Manifest.permission.CAMERA
        )
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) permissionsList.add(
            Manifest.permission.RECORD_AUDIO
        )

        if (permissionsList.size > 0) {
            requestPermissions(permissionsList.toTypedArray(), 101)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onNewMessage(message: MessageModel) {
        when(message.type){
            "call_response" -> {
                if (message.data == "user is not online") {
                    runOnUiThread {
                        Toast.makeText(this, "user is not reachable", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val intent = Intent(applicationContext, VideoCallActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

}

