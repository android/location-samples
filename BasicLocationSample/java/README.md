Basic Location Sample (Java)
============================

Demonstrates use of the Google Play services Location API to retrieve the last
known location for a device.

Introduction
============

This sample shows a simple way of getting a device's last known location, which
is usually equivalent to the device's current location.
The accuracy of the location returned is based on the location
permissions you've requested and the location sensors that are currently active
for the device.

To run this sample, **location must be enabled**.

This sample uses [FusedLocationProviderClient](https://developer.android.com/reference/com/google/android/gms/location/LocationServices.html).

Also see the [Kotlin version](../kotlin) of this sample.

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
