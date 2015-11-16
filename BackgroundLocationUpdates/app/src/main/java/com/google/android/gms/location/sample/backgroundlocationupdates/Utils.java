/**
 * Copyright 2015 Google Inc. All Rights Reserved.
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

package com.google.android.gms.location.sample.backgroundlocationupdates;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Utility methods used in this sample.
 */
public class Utils {
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";

    /**
     * The desired interval for active location updates. This interval is inexact and updates may
     * be faster than specified here if other applications are requesting location at a faster
     * interval. Used with {@link LocationRequest#setInterval(long)}.
     */
    private static final int UPDATE_INTERVAL_IN_MILLISECONDS = 30 * 1000; // 30 seconds.

    /**
     * Fastest rate for location updates. This interval is exact, and updates
     * will not be faster than this value. Used with
     * {@link LocationRequest#setFastestInterval(long)}.
     */
    private static final int FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * The interval for delivery of batched location updates. Used with
     * {@link LocationRequest#setMaxWaitTime(long)}.
     */
    public static final long BATCHED_REQUEST_MAX_WAIT_TIME = 60 * 60 * 1000; // One hour.

    /**
     * The minimum displacement between location updates. Used with
     * {@link LocationRequest#setSmallestDisplacement(float)}.
     */
    public static final float SMALLEST_DISPLACEMENT_IN_METERS = 20.0f;

    protected static LocationRequest createNonBatchedLocationRequest() {
        return new LocationRequest()
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(SMALLEST_DISPLACEMENT_IN_METERS);
    }

    protected static LocationRequest createBatchedLocationRequest() {
        return Utils.createNonBatchedLocationRequest().setMaxWaitTime(
                BATCHED_REQUEST_MAX_WAIT_TIME);
    }

    protected static boolean requestingLocationUpdates(Context context) {
        return context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    protected static void setRequestingLocationUpdates(Context context, boolean value) {
        context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, value)
                .apply();
    }

    protected static ArrayList<LatLng> getStoredLocationData(Context context) {
        SharedPreferences sp = context.getSharedPreferences(BuildConfig.APPLICATION_ID,
                Context.MODE_PRIVATE);
        String locationDataJson = sp.getString(Constants.KEY_LOCATION_DATA, "");
        if (TextUtils.isEmpty(locationDataJson)) {
            return new ArrayList<>();
        } else {
            return new Gson().fromJson(locationDataJson,
                    new TypeToken<ArrayList<LatLng>>() {}.getType());
        }
    }
}
