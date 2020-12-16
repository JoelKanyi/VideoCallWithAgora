package com.kanyideveloper.videocallwithagora

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
//A must imports so as to use agora apis
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22

    private var mRtcEngine: RtcEngine? = null
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {

        // Listen for the onUserOffline callback.
        // This callback occurs when the remote user leaves the channel or drops offline.
        override fun onUserOffline(uid: Int, reason: Int) {
            //runOnUiThread { onRemoteUserLeft(uid, reason) }
        }

        // Listen for the onUserMuterAudio callback.
        // This callback occurs when a remote user stops sending the audio stream.
        override fun onUserMuteAudio(uid: Int, muted: Boolean) {
           // runOnUiThread { onRemoteUserVoiceMuted(uid, muted)}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check the microphone access when running the app.
        // Initialize RtcEngine and join a channel after getting the permission.
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) ) {
            initAgoraEngineAndJoinChannel()
        }
    }

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            return false
        }
        return true
    }

    private fun initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine()
        joinChannel()
    }

    // Call the create method to initialize RtcEngine.
    private fun initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, getString(R.string.agora_app_id), mRtcEventHandler)

        } catch (e: Exception) {

            Log.d(TAG, "initializeAgoraEngine: ${e.stackTrace}")
            
            throw RuntimeException("NEED TO check rtc sdk init fatal error\n")
        }
    }

    private fun joinChannel() {
        // Call the joinChannel method to join a channel.
        // The uid is not specified. The SDK will assign one automatically.
        mRtcEngine!!.joinChannel(R.string.access_token.toString(), "demoChannel1", "Extra Optional Data", 0)
    }

    private fun leaveChannel() {
        mRtcEngine!!.leaveChannel()
    }

    fun onLocalAudioMuteClicked(view: View) {
        val iv: ImageView = view as ImageView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(resources.getColor(R.color.design_default_color_primary), PorterDuff.Mode.MULTIPLY)
        }

        // Stops/Resumes sending the local audio stream.
        mRtcEngine!!.muteLocalAudioStream(iv.isSelected)
    }

    fun onSwitchSpeakerphoneClicked(view: View) {
        val iv: ImageView = view as ImageView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(resources.getColor(R.color.design_default_color_primary), PorterDuff.Mode.MULTIPLY)
        }

        // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.
        mRtcEngine!!.setEnableSpeakerphone(view.isSelected())
    }

    fun onEncCallClicked(view: View) {
        finish()
    }

    override fun onStop() {
        super.onStop()
        leaveChannel()
    }
}