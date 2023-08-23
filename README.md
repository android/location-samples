> **Warning**
> This sample has been deprecated and is no longer being maintained.
> 
> Find the new location samples in the new [platform-samples repo](https://github.com/android/platform-samples/tree/main/samples/location).


~~Google Play Location & Context Samples~~
======================================

The [location and context APIs](https://developers.google.com/location-context/) harness the sensors and signals of mobile devices to provide awareness of user actions and their environment, enabling delightful and engaging experiences that simplify user interactions, provide assistance, and help users to better understand themselves.

This repo contains the following samples:

- [Activity Recognition](https://github.com/android/location-samples/tree/main/ActivityRecognition) (Kotlin) - Demonstrates the
[ActivityRecognitionApi](https://developers.google.com/android/reference/com/google/android/gms/location/ActivityRecognitionApi) to determine the user's current activity.

- [AwarenessApisSampleKotlin](https://github.com/android/location-samples/tree/main/AwarenessApisSampleKotlin) (Kotlin) - Demonstrates use of the [Awareness APIs](https://developers.google.com/android/reference/com/google/android/gms/awareness/Awareness#getSnapshotClient(android.app.Activity)) to intelligently react to the current situation of the user. 

- [ForegroundLocationUpdates](https://github.com/android/location-samples/tree/main/ForegroundLocationUpdates) (Kotlin) - Get updates about a device's location using a bound foreground service.

- [Geofencing](https://github.com/android/location-samples/tree/main/Geofencing) (Java) - Create geofences and process enter and exit transitions.

- [LocationAddress](https://github.com/android/location-samples/tree/main/LocationAddress) (Kotlin) - Use the [Geocode API](http://developer.android.com/reference/android/location/Geocoder.html) to display a device's location as an address.

- [LocationUpdatesBackgroundKotlin](https://github.com/android/location-samples/tree/main/LocationUpdatesBackgroundKotlin) (Kotlin) - Demonstrates the correct way to retrieve location updates in the background.

- [SleepSampleKotlin](https://github.com/android/location-samples/tree/main/SleepSampleKotlin) (Kotlin) - Demonstrates use of the Sleep API to recognize a user's sleep activity.


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

License
-------

These samples are distributed under the terms of the Apache License (Version 2.0).
See the [license](LICENSE) for more information.
