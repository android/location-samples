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

package com.google.android.gms.location.sample.activityrecognition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityTransitionResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetectedActivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val result = ActivityTransitionResult.extractResult(intent) ?: return

        if (result.transitionEvents.isNotEmpty()) {
            result.transitionEvents.forEach {
                // TODO persist and show in the app UI
                Log.d("ActivityRecognition", "Transition: ${it.activityType}, ${it.transitionType}")
            }
        }
    }
}
