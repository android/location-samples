Awareness API sample (Snapshot API)
====================================================

Demonstrates use of the [Awareness][1] APIs to intelligently react to the current situation of the
user.

Introduction
============

The Awareness API exposes five different context types, which include user activity, and nearby
beacons. These types enable your app to refine the user experience in new ways that were not
possible before. Your app can combine these context signals to make inferences about the current
situation of the user, and use this information to provide customized experiences, such as a
playlist suggestion when the user plugs in their headphones and starts to jog.

This sample demonstrates the Snapshot API to get information about the current environment of the
user.

Developers can find more about this API and other Awareness APIs in our [Getting Started page][2].

Users can request the current environment by pressing the "Request Snapshot" button.

**IMPORTANT NOTE**: The Awareness APIs (both Snapshot and Fend) require an API key in your manifest.
Check [this link][3] for more information.

[1]: https://developers.google.com/android/reference/com/google/android/gms/awareness/Awareness#getSnapshotClient(android.app.Activity)
[2]: https://developers.google.com/awareness/overview
[3]: https://developers.google.com/awareness/android-api/get-a-key

Prerequisites
--------------

- Android API Level >v26
- Google (Support) Repository

Getting Started
---------------

This sample uses the Gradle build system. To build this project, use the "gradlew build" command or
use "Import Project" in Android Studio.

Support
-------

- Stack Overflow: http://stackoverflow.com/questions/tagged/google-play-services

If you've found an error in this sample, please file an issue:
https://github.com/android/location-samples/issues

Patches are encouraged, and may be submitted according to the instructions in
CONTRIBUTING.md.