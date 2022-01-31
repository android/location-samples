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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.sample.activityrecognition.PlayServicesAvailableState.Initializing
import com.google.android.gms.location.sample.activityrecognition.PlayServicesAvailableState.PlayServicesAvailable
import com.google.android.gms.location.sample.activityrecognition.PlayServicesAvailableState.PlayServicesUnavailable
import com.google.android.gms.location.sample.activityrecognition.data.PlayServicesAvailabilityChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * View model for the main screen of the app (which also happens to be the only screen). This stores
 * state relevant to the UI so that state is properly maintained across configuration changes.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    playServicesAvailabilityChecker: PlayServicesAvailabilityChecker
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

}

enum class PlayServicesAvailableState {
    Initializing, PlayServicesUnavailable, PlayServicesAvailable
}
