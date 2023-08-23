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

package com.google.android.gms.location.sample.activityrecognition.data.db

import android.os.SystemClock
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.DetectedActivity
import java.time.Duration
import java.time.Instant

/**
 * Entity representing an activity transition event.
 */
@Entity
@TypeConverters(TimestampConverter::class)
data class ActivityTransitionRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val activityType: DetectedActivityType,
    val transitionType: DetectedTransitionType,
    val timestamp: Instant
)

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
    WALKING(DetectedActivity.WALKING);

    companion object {
        fun forType(type: Int): DetectedActivityType {
            return values().first { it.type == type }
        }
    }
}

/**
 * Enumerated form of Play Services [ActivityTransition] types.
 */
enum class DetectedTransitionType(private val type: Int) {
    ENTER(ActivityTransition.ACTIVITY_TRANSITION_ENTER),
    EXIT(ActivityTransition.ACTIVITY_TRANSITION_EXIT);

    companion object {
        fun forType(type: Int): DetectedTransitionType {
            return values().first { it.type == type }
        }
    }
}

/**
 * Utility to convert a Play Services [ActivityTransitionEvent] to a database entity type.
 */
fun ActivityTransitionEvent.asRecord() = ActivityTransitionRecord(
    activityType = DetectedActivityType.forType(activityType),
    transitionType = DetectedTransitionType.forType(transitionType),
    timestamp = elapsedRealTimeNanosToInstant(elapsedRealTimeNanos)
)

private fun elapsedRealTimeNanosToInstant(elapsedRealTimeNanos: Long): Instant {
    val currentElapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
    val currentInstant = Instant.now()
    return currentInstant - Duration.ofNanos(currentElapsedRealtimeNanos - elapsedRealTimeNanos)
}
