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

package com.google.android.gms.location.sample.activityrecognition.ui

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.location.sample.activityrecognition.ActivityRecognitionPermission
import com.google.android.gms.location.sample.activityrecognition.hasPermission
import com.google.android.gms.location.sample.activityrecognition.shouldShowRationaleFor

/**
 * State holder for activity recognition permission. Properties are implemented as State objects so
 * that they trigger a recomposition when the value changes (if the value is read within a
 * Composable scope). This also implements the behavior for requesting the permission and updating
 * the internal state afterward.
 *
 * This class should be initialized in `onCreate()` of the Activity. Sample usage:
 *
 * ```
 * override fun onCreate(savedInstanceState: Bundle?) {
 *     super.onCreate(savedInstanceState)
 *
 *     val permissionState = ActivityRecognitionPermissionState(this) { state ->
 *         if (state.permissionGranted) {
 *             // Do something requiring activity recognition permission
 *         }
 *     }
 *
 *     setContent {
 *         Button(
 *             onClick = { permissionState.requestPermission() }
 *         ) {
 *             Text("Click Me")
 *         }
 *     }
 * }
 * ```
 */
class ActivityRecognitionPermissionState (
    private val activity: ComponentActivity,
    private val onResult: (ActivityRecognitionPermissionState) -> Unit
) {
    /** Whether permission was granted for activity recognition. */
    var permissionGranted by mutableStateOf(false)
        private set

    /** Whether to show a rationale for permission to use activity recognition. */
    var needsRationale by mutableStateOf(false)
        private set

    /**
     * Whether to show a degraded experience (set after the permission is denied).
     */
    var showDegradedExperience by mutableStateOf(false)
        private set

    private val permissionLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            updateState()
            showDegradedExperience = !granted
            onResult(this)
        }

    init {
        updateState()
    }

    private fun updateState() {
        permissionGranted = activity.hasPermission(ActivityRecognitionPermission)
        needsRationale = activity.shouldShowRationaleFor(ActivityRecognitionPermission)
    }

    /**
     * Launch the permission request. Note that this may or may not show the permission UI if the
     * permission has already been granted or if the user has denied permission multiple times.
     */
    fun requestPermission() {
        permissionLauncher.launch(ActivityRecognitionPermission)
    }
}
