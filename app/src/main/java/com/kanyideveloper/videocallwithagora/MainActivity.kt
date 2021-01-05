package com.kanyideveloper.videocallwithagora

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQ_ID = 22

    private val REQUESTED_PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    )


    private lateinit var mRtcEngine: RtcEngine
    private var mCallEnd = false
    private var mMuted = false
    private lateinit var mLocalContainer: FrameLayout
    private lateinit var mRemoteContainer: RelativeLayout
    private  var mRemoteView: SurfaceView? = null
    private var mLocalView: SurfaceView?  = null
    private lateinit var mCallBtn: ImageView
    private lateinit var mMuteBtn: ImageView
    private lateinit var mSwitchCameraBtn: ImageView

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            runOnUiThread {
                Timber.d("Joined channel")
                Toast.makeText(applicationContext, "Joined Channel Successfully", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            runOnUiThread {
                Timber.d("first remote video decoded")
                setupRemoteVideo(uid) }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                Timber.d("user offline")
                onRemoteUserLeft()
            }
        }
    }

    private fun setupRemoteVideo(uid: Int) {

        if (mRemoteContainer.childCount >= 1) {
            return
        }else{
            mRemoteView = RtcEngine.CreateRendererView(baseContext)
            mRemoteContainer.addView(mRemoteView)
            mRtcEngine.setupRemoteVideo(VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid))
        }
    }

    private fun onRemoteUserLeft() {
        removeRemoteVideo()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.d("About to start everything")

        initUI()

        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (
                checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {

            Timber.d("Requested permission are ok")
            initAgoraEngineAndJoinChannel()
        }

    }

    private fun initUI() {
        mLocalContainer = findViewById(R.id.local_video_view)
        mRemoteContainer = findViewById(R.id.remote_video_view)
        mCallBtn = findViewById(R.id.btn_call)
        mMuteBtn = findViewById(R.id.btn_mute)
        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera)

        Timber.d("the UI has been initialized")
    }

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == PERMISSION_REQ_ID){
            if (
                    grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED){

                Timber.d("Failed")
                Toast.makeText(applicationContext, "Need permissions ${Manifest.permission.RECORD_AUDIO}  ${Manifest.permission.CAMERA}  ${Manifest.permission.WRITE_EXTERNAL_STORAGE} ${Manifest.permission.READ_PHONE_STATE}", Toast.LENGTH_LONG).show()
                finish()
                return
            }
            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            initAgoraEngineAndJoinChannel();
        }
    }

    private fun initAgoraEngineAndJoinChannel() {
        // This is our usual steps for joining
        // a channel and starting a call.
        Timber.d("about to initialize and join a channel")
        initializeAgoraEngine()
        setupVideoConfig()
        setupLocalVideo()
        joinChannel()
    }

    private fun initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, getString(R.string.agora_app_id), mRtcEventHandler)
            Timber.d("Successfully initialized the the agora engine")
        } catch (e: Exception) {
            Timber.d(e)
            throw RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e))
        }
    }

    private fun setupLocalVideo() {
        Timber.d(".................Setting up local video view........................")

        mLocalView = RtcEngine.CreateRendererView(baseContext)
       // mLocalView.setZOrderMediaOverlay(true)
        mLocalContainer.addView(mLocalView)

        // Set the local video view.
        mRtcEngine.setupLocalVideo(VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0))
        Timber.d("Set the local video view")
    }

    private fun setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo()

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT))
    }

    private fun joinChannel() {
        val token = getString(R.string.access_token)

        // Join a channel with a token.
        mRtcEngine.joinChannel(token, "demoChannel1", "Extra Optional Data", 0)
    }

    private fun removeRemoteVideo() {
        if (mRemoteView != null) {
            mRemoteContainer.removeView(mRemoteView)
        }
       mRemoteView = null
    }

    private fun leaveChannel() {
        // Leave the current channel.
        mRtcEngine.leaveChannel()
    }

    fun onLocalAudioMuteClicked(view: View) {
        mMuted = true
        mRtcEngine.muteLocalAudioStream(mMuted)
        val res: Int = if (mMuted){
            R.drawable.btn_mute_pressed
        }
        else R.drawable.btn_unmute_pressed

        mMuteBtn.setImageResource(res)
    }

    fun onSwitchCameraClicked(view: View) {
        mRtcEngine.switchCamera()
        Timber.d("Switched the camera")
    }

    fun onCallClicked(view: View){
        if (mCallEnd){
            startCall()
            mCallEnd = false
            mCallBtn.setImageResource(R.drawable.btn_endcall_pressed)
        }else{
            endCall()
            mCallEnd = true
            mCallBtn.setImageResource(R.drawable.btn_startcall_normal)
        }
        showButtons(!mCallEnd)
    }

    private fun endCall(){
        removeLocalVideo()
        removeRemoteVideo()
        leaveChannel()
    }

    private fun removeLocalVideo() {
        if (mLocalView != null){
            mLocalContainer.removeView(mLocalView)
        }
        mLocalView = null
    }

    private fun startCall(){
        setupLocalVideo()
        joinChannel()
    }

    private fun showButtons(boolean: Boolean){
        mMuteBtn.visibility = VISIBLE
        mSwitchCameraBtn.visibility = VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!mCallEnd){
            leaveChannel()
        }
        RtcEngine.destroy()
    }
}