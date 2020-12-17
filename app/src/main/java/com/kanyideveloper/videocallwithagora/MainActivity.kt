package com.kanyideveloper.videocallwithagora

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration


class MainActivity : AppCompatActivity() {

    private val LOG_TAG = "MainActivity"
    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    private val PERMISSION_REQ_ID_CAMERA = 22
    private lateinit var mMuteBtn: ImageView

    private val mCallEnd = false
    private var mMuted = false

    private var mLocalContainer: FrameLayout? = null
    private var mRemoteContainer: FrameLayout? = null
    private var mRemoteView: SurfaceView? = null

    private var mCallBtn: ImageView? = null
    private var mSwitchCameraBtn: ImageView? = null

    private var mRtcEngine: RtcEngine? = null
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {

        // Listen for the onFirstRemoteVideoDecoded callback.
        // This callback occurs when the first video frame of a remote user is received and decoded after the remote user successfully joins the channel.
        // You can call the setupRemoteVideo method in this callback to set up the remote video view.
        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            runOnUiThread { setupRemoteVideo(uid) }
        }

        // Listen for the onUserOffline callback.
        // This callback occurs when the remote user leaves the channel or drops offline.
        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread { onRemoteUserLeft() }
        }

    }

    private fun setupRemoteVideo(uid: Int) {

        val container = findViewById<FrameLayout>(R.id.remote_video_view)

        if (container.childCount >= 1) {
            return
        }

        // Create a SurfaceView object.
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        container.addView(surfaceView)

        // Set the remote video view.
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid))
    }

    private fun onRemoteUserLeft() {
        removeRemoteVideo()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()

        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
            initAgoraEngineAndJoinChannel()
        }

    }

    private fun initUI() {
        mLocalContainer = findViewById(R.id.local_video_view)
        mRemoteContainer = findViewById(R.id.remote_video_view)
        mCallBtn = findViewById(R.id.btn_call)
        mMuteBtn = findViewById(R.id.btn_mute)
        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera)
    }

    private fun initAgoraEngineAndJoinChannel() {

        // This is our usual steps for joining
        // a channel and starting a call.
        initializeAgoraEngine()
        setupVideoConfig()
        setupLocalVideo()
        joinChannel()
    }

    private fun joinChannel() {
        // Join a channel with a token.
        mRtcEngine!!.joinChannel(R.string.access_token.toString(), "demoChannel1", "Extra Optional Data", 0)
    }


    private fun setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine!!.enableVideo()

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine!!.setVideoEncoderConfiguration(VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT))
    }

    private fun setupLocalVideo() {
        // Enable the video module.
        mRtcEngine!!.enableVideo()

        val container = findViewById<FrameLayout>(R.id.local_video_view)

        // Create a SurfaceView object.
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        container.addView(surfaceView)
        // Set the local video view.
        mRtcEngine!!.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0))
    }

    private fun initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, getString(R.string.agora_app_id), mRtcEventHandler)
        } catch (e: Exception) {
            Log.e(LOG_TAG, Log.getStackTraceString(e))

            throw RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e))
        }
    }

    /**
     * Call the checkSelfPermission method to access the camera and the microphone of the Android device when launching the activity
     */
    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        Log.i(LOG_TAG, "checkSelfPermission $permission $requestCode")
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            return false
        }
        return true
    }

    private fun removeRemoteVideo() {
        if (mRemoteView != null) {
            mRemoteContainer!!.removeView(mRemoteView)
        }
        mRemoteView = null
    }


    private fun leaveChannel() {
        // Leave the current channel.
        mRtcEngine!!.leaveChannel()
    }

    fun onLocalAudioMuteClicked(view: View) {
        mMuted = !mMuted
        mRtcEngine!!.muteLocalAudioStream(mMuted)
        val res: Int = if (mMuted) R.drawable.btn_mute_pressed else R.drawable.btn_unmute_pressed
        mMuteBtn.setImageResource(res)
    }

    fun onSwitchCameraClicked(view: View) {
        mRtcEngine!!.switchCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        leaveChannel()
        RtcEngine.destroy()
        mRtcEngine = null
    }
}