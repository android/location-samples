/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.location.sample.locationupdatespendingintent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log

import com.google.android.gms.location.LocationResult

/**
 * Receiver for handling location updates.
 *
 * For apps targeting API level O
 * [android.app.PendingIntent.getBroadcast] should be used when
 * requesting location updates. Due to limits on background services,
 * [android.app.PendingIntent.getService] should not be used.
 *
 * Note: Apps running on "O" devices (regardless of targetSdkVersion) may receive updates
 * less frequently than the interval specified in the
 * [com.google.android.gms.location.LocationRequest] when the app is no longer in the
 * foreground.
 */
class LocationUpdatesBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_PROCESS_UPDATES == action) {
                val result = LocationResult.extractResult(intent)
                if (result != null) {
                    val locations = result.locations
                    Utils.setLocationUpdatesResult(context, locations)
                    Utils.sendNotification(context, Utils.getLocationResultTitle(context, locations))
                    Log.i(TAG, Utils.getLocationUpdatesResult(context))
                }
            }
        }
    }

    companion object {
        private val TAG = "LUBroadcastReceiver"

        internal val ACTION_PROCESS_UPDATES = "com.google.android.gms.location.sample.locationupdatespendingintent.action" + ".PROCESS_UPDATES"
    }
}
