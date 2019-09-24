Google Play Location Samples
============================

Samples that use
[Location APIs](http://developer.android.com/google/play-services/location.html)
to help you make your applications location aware.

This repo contains the following samples:

1. Basic Location Sample
 ([Java](https://github.com/googlesamples/android-play-location/tree/master/BasicLocationSample/java), [Kotlin](https://github.com/googlesamples/android-play-location/tree/master/BasicLocationSample/kotlin)):
Retrieve the last known location for a device.
1. [Location Updates](https://github.com/googlesamples/android-play-location/tree/master/LocationUpdates):
Get updates about a device's location.

1. [Location Updates using a PendingIntent](https://github.com/googlesamples/android-play-location/tree/master/LocationUpdatesPendingIntent):
Get updates about a device's location using a `PendingIntent`. Sample shows
implementation using an `IntentService` as well as a `BroadcastReceiver`.
1. [Location Updates using a Foreground Service](https://github.com/googlesamples/android-play-location/tree/master/LocationUpdatesForegroundService):
Get updates about a device's location using a bound and started foreground
service.
1. Location Address
([Java](https://github.com/googlesamples/android-play-location/tree/master/LocationAddress/java), [Kotlin](https://github.com/googlesamples/android-play-location/tree/master/LocationAddress/kotlin)):
Use the [Geocode API](http://developer.android.com/reference/android/location/Geocoder.html) to display a device's location as an address.
1. [Creating and Monitoring Geofences](https://github.com/googlesamples/android-play-location/tree/master/Geofencing):
Create geofences and process enter and exit transitions.
1. [Recognizing the User's Current Activity](https://github.com/googlesamples/android-play-location/tree/master/ActivityRecognition):
Use the
[ActivityRecognitionApi](https://developer.android.com/reference/com/google/android/gms/location/ActivityRecognitionApi.html) to determine the user's current activity.

Prerequisites
--------------

- Android API Level >v9
- Android Build Tools >v21
- Google Support Repository

Getting Started
---------------

These samples use the Gradle build system. To build a sample, use the
"gradlew build" command or use "Import Project" in Android Studio.

Support
-------

- Stack Overflow: http://stackoverflow.com/questions/tagged/google-play-services

If you've found an error in these samples, please file an issue:
https://github.com/android/location-samples/issues

Patches are encouraged, and may be submitted according to the instructions in
CONTRIBUTING.md.
