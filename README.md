# VideoCallWithAgora

a basic video call with the Agora Video SDK for Android.

## Setup Requirements
- Android Studio 3.0 or later
- Android SDK API Level 16 or higher
- A mobile device running Android 4.1 or later
- A valid Agora account and an App ID

## Integrate the SDK
    implementation 'io.agora.rtc:full-sdk:3.1.3'
    
## Permissions
```
   - <uses-permission android:name="android.permission.READ_PHONE_STATE" />   
   - <uses-permission android:name="android.permission.INTERNET" />
   - <uses-permission android:name="android.permission.RECORD_AUDIO" />
   - <uses-permission android:name="android.permission.CAMERA" />
   - <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
   - <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
   - <uses-permission android:name="android.permission.BLUETOOTH" />
   - <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
   - // Add the following permission if your scenario involves reading the external storage:
   - <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
   - // For devices running Android 10.0 or later, you also need to add the following permission:
   - <uses-permission android:name="android.permission.READ_PRIVILEGED_PHONE_STATE" />
   ```
   
   ## Prevent code obfuscation
   -keep class io.agora.**{*;}
   
   ## Import Classes
   - import io.agora.rtc.IRtcEngineEventHandler;
   - import io.agora.rtc.RtcEngine;
   - import io.agora.rtc.video.VideoCanvas;
   - import io.agora.rtc.video.VideoEncoderConfiguration;
   
   ## Get the device permission
   
       // Ask for Android device permissions at runtime.
    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_voice_chat_view)

      // If all the permissions are granted, initialize the RtcEngine object and join a channel.
      if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA,              PERMISSION_REQ_ID_CAMERA)) {
        initAgoraEngineAndJoinChannel()
      }
    }

    private fun checkSelfPermission(permission. String, requestCode: Int): Boolean {
      Log.i(LOG_TAG, "checkSelfPermission $permission $reuqestCode")
      if (ContextCompat.checkSelfPermission(this, 
              permission) != PackageManager.PERMISSION_GRANTED) {

        ActivityCompat.requestPermission(this
                arrayOf(permission),
                requestCode)
        return false
      }
      return true
    }
    
## Initialize RtcEngine
    // Initialize the RtcEngine object.
    private fun initializeAgoraEngine() {
      try {
        mRtcEngine = RtcEngine.create(baseContext, getString(R.string.agora_app_id), mRtcEventHandler)
      } catch (e: Exception) {
        Log.e(LOG_TAG, Log.getStackTraceString(e))

        throw RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e))
      }
    }
    
## Set the local video view
    private fun setupLocalVideo() {

      // Enable the video module.
      mRtcEngine!!.enableVideo()

      val container = findViewById(R.id.local_video_view_container) as FrameLayout

      // Create a SurfaceView object.
      val surfaceView = RtcEngine.createRendererView(baseContext)
      surfaceView.setZorderMediaOverlay(true)
      container.addView(surfaceView)
      // Set the local video view.
      mRtcEngine!!.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
    }
    
 ## Join a channel
     private fun joinChannel() {

      // Join a channel with a token.
      mRtcEngine!!.joinChannel(token, "demoChannel1", "Extra Optional Data", 0)
    }
    
## Set the remote video view
    // Listen for the onFirstRemoteVideoDecoded callback.
      // This callback occurs when the first video frame of a remote user is received and decoded after the remote user successfully joins the channel.
      // You can call the setupRemoteVideo method in this callback to set up the remote video view.
      override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
        runOnUiThread { setupRemoteVideo(uid) }
      }


    private fun setupRemoteVideo(uid: Int) {
      val container = findViewById(R.id.remote_video_view_container) as FrameLayout

      if (container.childCount >= 1) {
        return
      }

      // Create a SurfaceView object.
      val surfaceView = RtcEngine.CreateRendererView(baseContext)
      container.addView(surfaceView)

      // Set the remote video view.
      mRtcEngine!!.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid))
    }
    
## Leave the channel
    override fun onDestroy() {
      super.onDestroy()

      leaveChannel()
      RtcEngine.destroy()
      mRtcEngine = null
    }

    private fun leaveChannel() {
      // Leave the current channel.
      mRtcEngine!!.leaveChannel()
    }
    
## Run the project
Run the project on your Android device. You can see both the local and remote video views when you successfully start a one-to-one video call in the app.


## Getting Started

In order to get the app running yourself, you need to:

1.  clone this project
2.  Import the project into Android Studio
3.  Connect the android device with USB or just use your emulator
4.  In Android Studio, click on the "Run" button.

## Demo

<p float="left">
<img src="screenshots/Screenshot_20201225-114110.png" width=250/>
<img src="screenshots/Screenshot_20201225-114141.png" width=250/>
  </p>

## Support

- Found this project useful ❤️? Support by clicking the ⭐️ button on the upper right of this page. ✌️
- Notice anything else missing? File an issue 
- Feel free to contribute in any way to the project from typos in docs to code review are all welcome.

## Get in touch - Let's be friends

Please feel free to contact me if you have any questions, ideas or even if you just want to say hi. I’m up for talking, exchange ideas, collaborations or consults. You can connect with me through any of the avenues listed below:
- [Twitter](https://twitter.com/_joelkanyi)
- [Github](https://github.com/JoelKanyi)
- [Facebook](https://www.facebook.com/joel.kanyi.71)
- [LinkedIn](https://www.linkedin.com/in/joel-kanyi-037270174/) 

## References

- https://docs.agora.io/en/Video/start_call_android?platform=Android#import_class
- 
