/*
 * Copyright (C) 2021 Google Inc.
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

package com.google.android.gms.location.sample.foregroundlocation

import android.content.ServiceConnection
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.sample.foregroundlocation.PlayServicesAvailableState.Initializing
import com.google.android.gms.location.sample.foregroundlocation.PlayServicesAvailableState.PlayServicesAvailable
import com.google.android.gms.location.sample.foregroundlocation.PlayServicesAvailableState.PlayServicesUnavailable
import com.google.android.gms.location.sample.foregroundlocation.data.LocationPreferences
import com.google.android.gms.location.sample.foregroundlocation.data.LocationRepository
import com.google.android.gms.location.sample.foregroundlocation.data.PlayServicesAvailabilityChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    playServicesAvailabilityChecker: PlayServicesAvailabilityChecker,
    locationRepository: LocationRepository,
    private val locationPreferences: LocationPreferences,
    private val serviceConnection: ForegroundLocationServiceConnection
) : ViewModel(), ServiceConnection by serviceConnection {

    val playServicesAvailableState = flow {
        emit(
            if (playServicesAvailabilityChecker.isGooglePlayServicesAvailable()) {
                PlayServicesAvailable
            } else {
                PlayServicesUnavailable
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Initializing)

    val isReceivingLocationUpdates = locationRepository.isReceivingLocationUpdates
    val lastLocation = locationRepository.lastLocation

    fun toggleLocationUpdates() {
        if (isReceivingLocationUpdates.value) {
            stopLocationUpdates()
        } else {
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        serviceConnection.service?.startLocationUpdates()
        // Store that the user turned on location updates.
        // It's possible that the service was not connected for the above call. In that case, when
        // the service eventually starts, it will check the persisted value and react appropriately.
        viewModelScope.launch {
            locationPreferences.setLocationTurnedOn(true)
        }
    }

    private fun stopLocationUpdates() {
        serviceConnection.service?.stopLocationUpdates()
        // Store that the user turned off location updates.
        // It's possible that the service was not connected for the above call. In that case, when
        // the service eventually starts, it will check the persisted value and react appropriately.
        viewModelScope.launch {
            locationPreferences.setLocationTurnedOn(false)
        }
    }
}

enum class PlayServicesAvailableState {
    Initializing, PlayServicesUnavailable, PlayServicesAvailable
}
