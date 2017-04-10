Location Updates
================

Demonstrates how to use the Fused Location Provider API to get updates about a
device's location. The Fused Location Provider is part of the Google Play
services Location APIs.


Introduction
============

This sample builds on the BasicLocationSample sample included in this repo,
and allows the user to request periodic location updates. In response, the API
updates the app periodically with the best available location, based on the
currently-available location providers such as WiFi and GPS (Global
Positioning System). The accuracy of the location is also determined by the
location permissions you've requested (we use the ACCESS_FINE_LOCATION here)
and the options you set in the location request.


This sample uses the
[FusedLocationProviderClient] (https://developer.android.com/reference/com/google/android/gms/location/LocationServices.html).

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
