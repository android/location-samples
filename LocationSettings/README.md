Location Settings
=================


Demonstrates how to use the Location SettingsApi to check if a device has the
location settings required by an application, and optionally provide a location
dialog to update the device's location settings.

Introduction
============

This sample builds on the LocationUpdates sample included in this repo,
and allows the user to update the device's location settings using a location
dialog.

Uses the
[SettingsApi](https://developer.android.com/reference/com/google/android/gms/location/SettingsApi.html)
to ensure that the device's system settings are properly configured for the
app's location needs. When making a request to Location services, the device's
system settings may be in a state that prevents the app from obtaining the
location data that it needs. For example, GPS or Wi-Fi scanning may be switched
off. The
[SettingsApi](https://developer.android.com/reference/com/google/android/gms/location/SettingsApi.html)
makes it possible to determine if a device's system settings are adequate for
the location request, and to optionally invoke a dialog that allows the user to
 enable the necessary settings.

 This sample allows the user to request location updates using the
 ACCESS_FINE_LOCATION setting (as specified in AndroidManifest.xml). The sample
 requires that the device has location enabled and set to the "High accuracy"
 mode. If location is not enabled, or if the location mode does not permit high
 accuracy determination of location, the activity uses the `SettingsApi`
 to invoke a dialog to allow the user to upgrade the device's settings.

This sample uses
[Google Play services (GoogleApiClient)](https://developer.android.com/reference/com/google/android/gms/common/api/GoogleApiClient.html)
and the
[FusedLocationApi] (https://developer.android.com/reference/com/google/android/gms/location/LocationServices.html).

To run this sample, **disable location or pick any location mode other than
the "High accuracy" mode.**


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
