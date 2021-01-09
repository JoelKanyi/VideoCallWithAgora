# VideoCallWithAgora

a basic video call with the Agora Video SDK for Android.

## Setup Requirements
- Android Studio 3.0 or later
- Android SDK API Level 16 or higher
- A mobile device running Android 4.1 or later
- A valid Agora account and an App ID

## Integrate the SDK
dependencies {
    // Get the latest version number through the release notes.
    implementation 'io.agora.rtc:full-sdk:3.1.3'
}


## Getting Started

In order to get the app running yourself, you need to:

1.  clone this project
2.  Import the project into Android Studio
3.  Connect the android device with USB or just use your emulator
4.  In Android Studio, click on the "Run" button.

## Libraries

Libraries used in the whole application are:

- [Kotlin](https://developer.android.com/kotlin) - Kotlin is a programming language that can run on JVM. Google has announced Kotlin as one of its officially supported programming languages in Android Studio; and the Android community is migrating at a pace from Java to Kotlin
- [Viewmodel](https://developer.android.com/topic/libraries/architecture/viewmodel) -The ViewModel class is designed to store and manage UI-related data in a lifecycle conscious way
- [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) -  A lifecycle-aware data holder with the observer pattern
- [Kotlin Coroutines](https://developer.android.com/kotlin/coroutines) - A concurrency design pattern that you can use on Android to simplify code that executes asynchronously
- [Retofit](https://square.github.io/retrofit) -  Retrofit is a REST Client for Java and Android by Square inc under Apache 2.0 license. Its a simple network library that used for network transactions. By using this library we can seamlessly capture JSON response from web service/web API.
- [Timber](https://github.com/JakeWharton/timber) - Timber is a logging utility class built on top of Android’s Log class. While in development, we usually end up writing lot of log statements and before the release, we’ll cleanup the log statements by removing them manually (even though logs can be disabled in release build). This tedious process can be avoided easily by using Timber.

## Demo

<p float="left">
<img src="screenshots/Screenshot_20201225-114110.png" width=250/>
<img src="screenshots/Screenshot_20201225-114141.png" width=250/>
  </p>

## Contributors

- Thanks to [Stevdza-San](https://www.youtube.com/channel/UCYLAirIEMMXtWOECuZAtjqQ) for amazing tutorial on the MVVM and android architectural components

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

- https://developer.android.com/jetpack/
- https://www.youtube.com/channel/UCYLAirIEMMXtWOECuZAtjqQ
