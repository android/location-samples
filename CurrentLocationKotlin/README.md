Current Location (Kotlin)
===========================================
Demonstrates retrieving the current location in Kotlin.

Introduction
============
This app allows a user to receive the current location without subscribing to location updates (and
then unsubscribe once you retrieve the location).

Users have four options in Android 11+ regarding location:
* One time only
* Allow while app is in use, i.e., while app is in foreground
* Allow all the time
* Not allow location at all

In this samples case, we only ask for 'while-in-use' location access via the FINE location permission
(`android.permission.ACCESS_FINE_LOCATION`), so the **All the time** access will not show up as an
option.

Foreground location access, also known as 'while-in-use' access, includes **One time only** and
**Allow while app is in use** location access.

To run this sample, **location must be enabled**.

**IMPORTANT NOTE**: You should generally prefer 'while-in-use' for location requests.

Prerequisites
--------------

- Android API Level >v27
- Google Support Repository

Getting Started
---------------
This sample uses the Gradle build system. To build this project, use the "gradlew build" command
or use "Import Project" in Android Studio.

The sample also work on Android 11, but you will need to change the build.gradle to target 30.

Support
-------

- Stack Overflow: http://stackoverflow.com/questions/tagged/google-play-services

If you've found an error in this sample, please file an issue:
https://github.com/android/location-samples/issues

Patches are encouraged, and may be submitted according to the instructions in CONTRIBUTING.md.