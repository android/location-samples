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


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder

import java.text.DateFormat
import java.util.Date

/**
 * Utility methods used in this sample.
 */
internal object Utils {

    val KEY_LOCATION_UPDATES_REQUESTED = "location-updates-requested"
    val KEY_LOCATION_UPDATES_RESULT = "location-update-result"
    val CHANNEL_ID = "channel_01"

    fun setRequestingLocationUpdates(context: Context, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_LOCATION_UPDATES_REQUESTED, value)
                .apply()
    }

    fun getRequestingLocationUpdates(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_LOCATION_UPDATES_REQUESTED, false)
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    fun sendNotification(context: Context, notificationDetails: String) {
        // Create an explicit content Intent that starts the main Activity.
        val notificationIntent = Intent(context, MainActivity::class.java)

        notificationIntent.putExtra("from_notification", true)

        // Construct a task stack.
        val stackBuilder = TaskStackBuilder.create(context)

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity::class.java)

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent)

        // Get a PendingIntent containing the entire back stack.
        val notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        // Get a notification builder that's compatible with platform versions >= 4
        val builder = NotificationCompat.Builder(context)

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(context.resources,
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle("Location update")
                .setContentText(notificationDetails)
                .setContentIntent(notificationPendingIntent)

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true)

        // Get an instance of the Notification manager
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.app_name)
            // Create the channel for the notification
            val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel)

            // Channel ID
            builder.setChannelId(CHANNEL_ID)
        }

        // Issue the notification
        mNotificationManager.notify(0, builder.build())
    }


    /**
     * Returns the title for reporting about a list of [Location] objects.
     *
     * @param context The [Context].
     */
    fun getLocationResultTitle(context: Context, locations: List<Location>): String {
        val numLocationsReported = context.resources.getQuantityString(
                R.plurals.num_locations_reported, locations.size, locations.size)
        return numLocationsReported + ": " + DateFormat.getDateTimeInstance().format(Date())
    }

    /**
     * Returns te text for reporting about a list of  [Location] objects.
     *
     * @param locations List of [Location]s.
     */
    private fun getLocationResultText(context: Context, locations: List<Location>): String {
        if (locations.isEmpty()) {
            return context.getString(R.string.unknown_location)
        }
        val sb = StringBuilder()
        for (location in locations) {
            sb.append("(")
            sb.append(location.latitude)
            sb.append(", ")
            sb.append(location.longitude)
            sb.append(")")
            sb.append("\n")
        }
        return sb.toString()
    }

    fun setLocationUpdatesResult(context: Context, locations: List<Location>) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_LOCATION_UPDATES_RESULT, getLocationResultTitle(context, locations)
                        + "\n" + getLocationResultText(context, locations))
                .apply()
    }

    fun getLocationUpdatesResult(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_LOCATION_UPDATES_RESULT, "")
    }
}
