/*
 * Copyright 2022 Google, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.location.sample.activityrecognition.data

import com.google.android.gms.location.DetectedActivity

/**
 * Enumerated form of Play Services [DetectedActivity] types. Note: some types are not supported by
 * activity transitions API, only supported types are enumerated here.
 */
enum class DetectedActivityType(val type: Int) {
    IN_VEHICLE(DetectedActivity.IN_VEHICLE),
    ON_BICYCLE(DetectedActivity.ON_BICYCLE),
    ON_FOOT(DetectedActivity.ON_FOOT),
    RUNNING(DetectedActivity.RUNNING),
    STILL(DetectedActivity.STILL),
    WALKING(DetectedActivity.WALKING)
}
