Creating and Monitoring Geofences
=================================

Demonstrates how to create and remove geofences using the
[GeofencingApi](https://developer.android.com/reference/com/google/android/gms/location/GeofencingApi.html).
Monitor geofence transitions and creates notifications whenever a device enters or exits a geofence.

Introduction
============

Geofencing combines awareness of the user's current location with awareness of
nearby features, defined as the user's proximity to locations that may be of
interest. To mark a location of interest, you specify its latitude and
longitude. To adjust the proximity for the location, you add a radius. The
latitude, longitude, and radius define a geofence. You can have multiple active
geofences at one time.

Location Services treats a geofences as an area rather than as a points and
proximity. This allows it to detect when the user enters or exits a geofence.
For each geofence, you can ask Location Services to send you entrance events,
exit events, or both. You can also limit the duration of a geofence by
specifying an expiration duration in milliseconds. After the geofence expires,
Location Services automatically removes it.

To run this sample, **location must be enabled**.

Prerequisites
--------------

- Android API Level >v9
- Android Build Tools >v21
- Google Support Repository

Getting Started
---------------

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

Support
-------

- Stack Overflow: http://stackoverflow.com/questions/tagged/google-play-services

If you've found an error in this sample, please file an issue:
https://github.com/googlesamples/android-play-location/issues

Patches are encouraged, and may be submitted according to the instructions in
CONTRIBUTING.md.

License
-------

Copyright 2014 Google, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
