Location Updates (Kotlin)
=========================

Demonstrates how to use the
[Geocode API](http://developer.android.com/reference/android/location/Geocoder.html)
and reverse geocoding to display a device's location as an address.

Introduction
============

This sample builds on the BasicLocationSample and the LocationUdpates samples
included in this repo. Those samples work with latitude and longitude values
only. While latitude and longitude are useful for calculating distance or
displaying a map position, in many cases the address of the location is more
useful. The Android framework location APIs provide a
[Geocode API](http://developer.android.com/reference/android/location/Geocoder.html)
which contains a
[getFromLocation()]http://developer.android.com/reference/android/location/Geocoder.html#getFromLocation(double, double, int))
method that returns an estimated street address corresponding to a given
latitude and longitude. This sample uses the `getFromLocation()` method to do
location address lookup, an IntentService to fetch the location address, and a
ResultReceiver to process results sent by the IntentService.

To run this sample, **location must be enabled**.

Prerequisites
--------------

- Android API Level >v15
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

Copyright 2018 Google LLC

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
