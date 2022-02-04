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
import com.google.android.gms.location.sample.activityrecognition.data.ActivityTransitionManager
import com.google.android.gms.location.sample.activityrecognition.data.AppPreferences
import com.google.android.gms.location.sample.activityrecognition.data.PlayServicesAvailabilityChecker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Receiver that will restore the app's registration for activity transition updates if they were
 * interrupted, e.g. because the device rebooted.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var appPreferences: AppPreferences

    @Inject lateinit var playServicesAvailabilityChecker: PlayServicesAvailabilityChecker

    @Inject lateinit var activityTransitionManager: ActivityTransitionManager

    private val coroutineScope = MainScope()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in VALID_ACTIONS) return

        coroutineScope.launch {
            val turnOn = appPreferences.isActivityTransitionUpdatesTurnedOn.first()
            if (!turnOn) return@launch

            // The user previously turned on activity transition updates. Try to request updates.
            val success = playServicesAvailabilityChecker.isGooglePlayServicesAvailable() &&
                context.hasPermission(ActivityRecognitionPermission) &&
                activityTransitionManager.requestActivityTransitionUpdates()

            if (!success) {
                // Something went wrong, such as our app losing the required permission. Change the
                // preference value so the user has to start activity transition updates again. A
                // real app may want to show a notification to inform the user that it could not
                // resume functionality.
                appPreferences.setActivityTransitionUpdatesTurnedOn(false)
            }
        }
    }

    private companion object {
        val VALID_ACTIONS = listOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON"
        )
    }
}
