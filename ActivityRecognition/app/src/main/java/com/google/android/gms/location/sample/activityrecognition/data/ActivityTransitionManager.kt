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

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.sample.activityrecognition.DetectedActivityReceiver
import com.google.android.gms.location.sample.activityrecognition.data.db.DetectedActivityType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Class which controls registration and unregistration for activity transition updates.
 */
class ActivityTransitionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val activityRecognitionClient: ActivityRecognitionClient
) {

    private val pendingIntent by lazy {
        PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, DetectedActivityReceiver::class.java),
            // Note: must use FLAG_MUTABLE in order for Play Services to add the result data to the
            // intent starting in API level 31. Otherwise the BroadcastReceiver will be started but
            // the Intent will have no data.
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    /**
     * Register for activity transition updates and return whether the call succeeded.
     */
    suspend fun requestActivityTransitionUpdates(): Boolean {
        // Real apps will want transitions that make sense for a particular feature. For example,
        // an app that changes its behavior while the user is driving a vehicle will want two
        // transitions:
        //   - DetectedActivity.IN_VEHICLE with ActivityTransition.ACTIVITY_TRANSITION_ENTER
        //   - DetectedActivity.IN_VEHICLE with ActivityTransition.ACTIVITY_TRANSITION_EXIT
        //
        // This sample will show the most recent transitions of any type, so request updates for all
        // DetectedActivity types. We can request just ActivityTransition.ACTIVITY_TRANSITION_ENTER
        // transitions, because entering a new activity type implies exiting the old one.
        val request = ActivityTransitionRequest(
            DetectedActivityType.values().map {
                ActivityTransition.Builder()
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .setActivityType(it.type)
                    .build()
            }
        )
        // This await() suspends until the task completes. For codebases not using coroutines, you
        // can either
        // - use Tasks.await(task) to block until the task completes
        // - use addOnCompleteListener() to be notified asynchronously when the task completes
        val task =
            activityRecognitionClient.requestActivityTransitionUpdates(request, pendingIntent)
        task.await()
        return task.isSuccessful
    }

    suspend fun removeActivityTransitionUpdates() {
        // This await() suspends until the task completes. For codebases not using coroutines, you
        // can either
        // - use Tasks.await(task) to block until the task completes
        // - use addOnCompleteListener() to be notified asynchronously when the task completes
        activityRecognitionClient.removeActivityTransitionUpdates(pendingIntent).await()
    }
}
