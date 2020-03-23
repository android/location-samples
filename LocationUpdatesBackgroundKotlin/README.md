Location Updates in the Background (Kotlin)
===========================================
Demonstrates retrieving location updates in the background.

Introduction
============
This app allows a user to receive location updates in the background via a `PendingIntent`.

If you are just interested in seeing the code that subscribes to a location request, please
review the **`MyLocationManager`** file and the **`LocationUpdatesBroadcastReceiver`** file for the
BroadcastReceiver triggered by location changes.

Users have four options in Android 11+ regarding location:
* One time only
* Allow while app is in use, i.e., while app is in foreground
* Allow all the time
* Not allow location at all

In addition to the FINE location permission (`android.permission.ACCESS_FINE_LOCATION`), if you do
have an approved use case for receiving location updates in the background, it will require an
additional permission (`android.permission.ACCESS_BACKGROUND_LOCATION`).

To run this sample, **location must be enabled**.

**IMPORTANT NOTE**: You should generally prefer 'while-in-use' for location updates, i.e., receiving
location updates while the app is in use and create a foreground service (tied to a Notification)
when the user navigates away from the app. To learn how to do that instead, review the
[Receive location updates in Android 10 with Kotlin](https://codelabs.developers.google.com/codelabs/while-in-use-location/index.html?index=..%2F..index#0)
codelab.

Prerequisites
--------------

- Android API Level >v15
- Google Support Repository

Getting Started
---------------
This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

The sample also work on Android R, but you will need to change the build.gradle to target R.

Support
-------

- Stack Overflow: http://stackoverflow.com/questions/tagged/google-play-services

If you've found an error in this sample, please file an issue:
https://github.com/android/location-samples/issues

Patches are encouraged, and may be submitted according to the instructions in CONTRIBUTING.md.
