Background Location Updates
===========================

Demonstrates use of batched location updates using
[FusedLocationProviderApi](https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderApi).

Introduction
============

This sample shows ways to toggle between two modes of location updates:
non-batched mode for when the main activity is visible, and batched updates
for when it is not. Updates are received in a Service, which is bound to the main
activity and also started by it. Location data is plotted to a Google Map.

Prerequisites
--------------

- Android API Level >v9
- Android Build Tools >v21
- Google Support Repository

Getting Started
---------------

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

While use of Location Services by itself does not require creating a project
on [Google Developer Console](https://console.developers.google.com/), this
sample uses the Google Maps Android API. Use the
[instructions for Signup and API Keys]( https://developers.google.com/maps/documentation/android-api/signup?hl=en)
to generate a key for running this project, and enter the key in
`AndroidManifest.xml`.


Support
-------

- Stack Overflow: http://stackoverflow.com/questions/tagged/google-play-services

If you've found an error in this sample, please file an issue:
https://github.com/googlesamples/android-play-location/issues

Patches are encouraged, and may be submitted according to the instructions in
CONTRIBUTING.md.

License
-------

Copyright 2015 Google, Inc.

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
