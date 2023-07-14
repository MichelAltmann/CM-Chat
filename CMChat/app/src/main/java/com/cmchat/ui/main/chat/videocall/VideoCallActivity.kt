package com.cmchat.ui.main.chat.videocall

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.SessionConfiguration
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.view.Surface
import android.view.TextureView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cmchat.application.Application
import com.cmchat.cmchat.R
import com.cmchat.cmchat.databinding.ActivityVideoCallBinding
import com.cmchat.webrtc.RTCClient
import com.cmchat.webrtc.SocketRepository
import com.cmchat.webrtc.models.MessageModel
import com.cmchat.webrtc.models.PeerConnectionObserver
import com.cmchat.webrtc.util.NewMessageInterface
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import java.util.Collections


class VideoCallActivity : AppCompatActivity(){

    private var _binding : ActivityVideoCallBinding? = null
    private val binding get() = _binding!!

    private var rtcClient : RTCClient? = null
    private val application by lazy {
        applicationContext as Application
    }
    private val socketRepository by lazy {
        application.getSocketRepository()
    }
    private val user by lazy {
        application.getUser()
    }

    private lateinit var friend : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        friend = intent.getStringExtra("friend") as String

        init()
    }

    private fun init() {
        rtcClient = RTCClient(application, user.username, socketRepository, object : PeerConnectionObserver() {

            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
            }

            override fun onAddStream(p0: MediaStream?) {
                super.onAddStream(p0)
            }

        })



    }

}