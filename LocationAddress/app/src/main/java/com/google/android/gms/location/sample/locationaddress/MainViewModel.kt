/*
 * Copyright (C) 2021 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.location.sample.locationaddress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.sample.locationaddress.UiState.Initializing
import com.google.android.gms.location.sample.locationaddress.UiState.PlayServicesAvailable
import com.google.android.gms.location.sample.locationaddress.UiState.PlayServicesUnavailable
import com.google.android.gms.location.sample.locationaddress.data.FormattedAddress
import com.google.android.gms.location.sample.locationaddress.data.GeocodingApi
import com.google.android.gms.location.sample.locationaddress.data.LocationApi
import com.google.android.gms.location.sample.locationaddress.data.PlayServicesAvailabilityChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    availabilityChecker: PlayServicesAvailabilityChecker,
    private val locationApi: LocationApi,
    private val geocodingApi: GeocodingApi
) : ViewModel() {

    val uiState = flow {
        emit(
            if (availabilityChecker.isGooglePlayServicesAvailable()) {
                PlayServicesAvailable
            } else {
                PlayServicesUnavailable
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Initializing)

    var addressList by mutableStateOf(emptyList<FormattedAddress>())
        private set

    var showProgress by mutableStateOf(false)
        private set

    val maxResultsRange = 1..7
    var maxResults by mutableStateOf(1)
        private set

    fun updateMaxResults(max: Int) {
        maxResults = max.coerceIn(maxResultsRange)
    }

    fun getCurrentAddress() {
        viewModelScope.launch {
            showProgress = true
            val location = locationApi.getCurrentLocation()
            val addresses = if (location != null) {
                geocodingApi.getFromLocation(location, maxResults)
            } else {
                emptyList()
            }
            addressList = addresses
            showProgress = false
        }
    }
}

enum class UiState {
    Initializing, PlayServicesUnavailable, PlayServicesAvailable
}
