package com.cmchat.webrtc

import android.app.Application
import org.webrtc.Camera2Enumerator
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.Observer
import org.webrtc.PeerConnectionFactory
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoCapturer
import java.lang.IllegalStateException

class RTCClient(
    private val application: Application,
    private val username: String,
    private val socketRepository : SocketRepository,
    private val observer: Observer
) {

    init {
        initPeerConnectionFactory(application)
    }

    private val eglContext = EglBase.create()
    private val peerConnectionFactory by lazy {
        createPeerConnectionFactory()
    }
    private val iceServer = listOf(
        PeerConnection.IceServer.builder("stun:stun.1.google.com:19302").createIceServer()
    )
    private val peerConnection by lazy {
        createPeerConnection(observer)
    }
    private val localVideoSource by lazy {
        peerConnectionFactory.createVideoSource(false)
    }
    private val localAudioSource by lazy {
        peerConnectionFactory.createAudioSource(MediaConstraints())
    }

    private fun initPeerConnectionFactory(application: Application) {
        val peerConnectionOption = PeerConnectionFactory.InitializationOptions.builder(application)
            .setEnableInternalTracer(true).setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()

        PeerConnectionFactory.initialize(peerConnectionOption)
    }



    private fun createPeerConnectionFactory(): PeerConnectionFactory {
        return PeerConnectionFactory.builder()
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglContext.eglBaseContext, true, true))
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglContext.eglBaseContext)).setOptions(PeerConnectionFactory.Options().apply {
                disableEncryption = true
                disableNetworkMonitor = true
            }).createPeerConnectionFactory()
    }

    private fun createPeerConnection(observer: PeerConnection.Observer) : PeerConnection? {
        return peerConnectionFactory.createPeerConnection(iceServer, observer)
    }

    fun initializeSurfaceView(surface:SurfaceViewRenderer) {
        surface.run {
            setEnableHardwareScaler(true)
            setMirror(true)
            init(eglContext.eglBaseContext, null)
        }
    }

    fun startLocalVideo(surface: SurfaceViewRenderer) {
        val surfaceTextureHelper = SurfaceTextureHelper.create(Thread.currentThread().name, eglContext.eglBaseContext)
        val videoCapturer = getVideoCapturer(application)
        videoCapturer.initialize(surfaceTextureHelper, surface.context, localVideoSource.capturerObserver)
        videoCapturer.startCapture(720,480,30)
        val localVideoTrack = peerConnectionFactory.createVideoTrack("local_video_track", localVideoSource)
        localVideoTrack.addSink(surface)
        val localAudioTrack = peerConnectionFactory.createAudioTrack("local_audio_track", localAudioSource)
        val localStream = peerConnectionFactory.createLocalMediaStream("local_stream")
        localStream.addTrack(localVideoTrack)
        localStream.addTrack(localAudioTrack)

        peerConnection?.addStream(localStream)

    }

    private fun getVideoCapturer(application: Application) : VideoCapturer {
        return Camera2Enumerator(application).run {
            deviceNames.find {
                isFrontFacing(it)
            }?.let {
                createCapturer(it, null)
            }?: throw IllegalStateException()
        }
    }

}