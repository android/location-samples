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

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.sample.activityrecognition.PlayServicesAvailableState.Initializing
import com.google.android.gms.location.sample.activityrecognition.PlayServicesAvailableState.PlayServicesAvailable
import com.google.android.gms.location.sample.activityrecognition.PlayServicesAvailableState.PlayServicesUnavailable
import com.google.android.gms.location.sample.activityrecognition.data.ActivityTransitionManager
import com.google.android.gms.location.sample.activityrecognition.data.AppPreferences
import com.google.android.gms.location.sample.activityrecognition.data.PlayServicesAvailabilityChecker
import com.google.android.gms.location.sample.activityrecognition.data.db.ActivityTransitionDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

/**
 * View model for the main screen of the app (which also happens to be the only screen). This stores
 * state relevant to the UI so that state is properly maintained across configuration changes.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    playServicesAvailabilityChecker: PlayServicesAvailabilityChecker,
    private val appPreferences: AppPreferences,
    private val activityTransitionManager: ActivityTransitionManager,
    private val activityTransitionDao: ActivityTransitionDao
) : ViewModel() {

    /**
     * Whether Google Play Services are available. Using [stateIn] converts this to a hot flow,
     * meaning it can be active even when there are no consumers. It will be started immediately
     * with an initial value of [Initializing], then updated once we've checked for Play Services.
     */
    val playServicesAvailableState = flow {
        emit(
            if (playServicesAvailabilityChecker.isGooglePlayServicesAvailable()) {
                PlayServicesAvailable
            } else {
                PlayServicesUnavailable
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Initializing)

    val isActivityTransitionUpdatesTurnedOn = appPreferences.isActivityTransitionUpdatesTurnedOn
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val transitionEvents = activityTransitionDao.getMostRecent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var errorMessages by mutableStateOf(emptyList<ErrorMessage>())
        private set

    fun toggleActivityTransitionUpdates() {
        if (isActivityTransitionUpdatesTurnedOn.value) {
            stopActivityTransitionUpdates()
        } else {
            startActivityTransitionUpdates()
        }
    }

    private fun startActivityTransitionUpdates() {
        viewModelScope.launch {
            if (activityTransitionManager.requestActivityTransitionUpdates()) {
                appPreferences.setActivityTransitionUpdatesTurnedOn(true)
            } else {
                errorMessages = errorMessages + ErrorMessage(R.string.error_requesting_updates)
            }
        }
    }

    private fun stopActivityTransitionUpdates() {
        viewModelScope.launch {
            activityTransitionManager.removeActivityTransitionUpdates()
            appPreferences.setActivityTransitionUpdatesTurnedOn(false)
            activityTransitionDao.deleteAll()
        }
    }

    fun removeMessage(errorMessage: ErrorMessage) {
        errorMessages = errorMessages.filterNot { it == errorMessage }
    }
}

enum class PlayServicesAvailableState {
    Initializing, PlayServicesUnavailable, PlayServicesAvailable
}

data class ErrorMessage(@StringRes val resId: Int, val time: Instant = Instant.now())
