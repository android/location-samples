Recognizing the User's Current Activity
=======================================

Demonstrates use of the [ActivityRecognitionApi][1] to recognize a user's current
activity, such as walking, driving, or standing still.

Introduction
============

Demonstrates use of the [ActivityRecognitionApi][1] to recognize a user's current
activity, such as walking, driving, or standing still.

Users can request activity updates by pressing the "Request Updates" button,
and stop receiving updates using the "Remove Updates" button.

The sample uses an `IntentService` to process detected activity changes, which
are sent using [ActivityRecognitionResult][2] objects. The IntentService gets a
list of probable detected activities and broadcasts them through a BroadcastReceiver. See the
[DetectedActivity][3] class for a list of DetectedActivity types. Each
`DetectedActivity` is associdated with a confidence level, which is an int
between 0 and 100.

To run this sample, **location must be enabled**.

[1]: https://developer.android.com/reference/com/google/android/gms/location/ActivityRecognitionApi.html

[2]: https://developer.android.com/reference/com/google/android/gms/location/ActivityRecognitionResult.html

[3]: https://developer.android.com/reference/com/google/android/gms/location/DetectedActivity.html

Prerequisites
--------------

- Android API Level >v9
- Android Build Tools >v21
- Google (Support) Repository

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
