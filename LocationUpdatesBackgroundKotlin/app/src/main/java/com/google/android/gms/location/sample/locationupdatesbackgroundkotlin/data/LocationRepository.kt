/*
 * Copyright (C) 2020 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.location.sample.locationupdatesbackgroundkotlin.data

import android.content.Context

import androidx.lifecycle.LiveData

import java.util.UUID
import java.util.concurrent.Executors

import com.google.android.gms.location.sample.locationupdatesbackgroundkotlin.data.db.MyLocationDatabase
import com.google.android.gms.location.sample.locationupdatesbackgroundkotlin.data.db.MyLocationEntity

private const val TAG = "LocationRepository"

/**
 * Access point for database (MyLocation data) and location APIs (start/stop location tracking and
 * checking tracking status).
 */
class LocationRepository private constructor(private val myLocationDatabase: MyLocationDatabase,
                                             private val myLocationManager: MyLocationManager
                                         ) {

    // Database related fields/methods:
    private val locationDao = myLocationDatabase.locationDao()

    private val executor = Executors.newSingleThreadExecutor()

    fun getLocations(): LiveData<List<MyLocationEntity>> = locationDao.getLocations()

    // Not being used now but could in future versions.
    fun getLocation(id: UUID): LiveData<MyLocationEntity> = locationDao.getLocation(id)

    // Not being used now but could in future versions.
    fun updateLocation(myLocationEntity: MyLocationEntity) {
        executor.execute {
            locationDao.updateLocation(myLocationEntity)
        }
    }

    fun addLocation(myLocationEntity: MyLocationEntity) {
        executor.execute {
            locationDao.addLocation(myLocationEntity)
        }
    }

    // Location related fields/methods:
    val trackingLocation: LiveData<Boolean> = myLocationManager.trackingLocation

    fun startLocationUpdates() = myLocationManager.startLocationUpdates()

    fun stopLocationUpdates() = myLocationManager.stopLocationUpdates()

    companion object {
        private var INSTANCE: LocationRepository? = null

        fun getInstance(context: Context): LocationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocationRepository(
                    MyLocationDatabase.getInstance(context),
                    MyLocationManager.getInstance(context))
                    .also { INSTANCE = it }
            }
        }
    }
}
