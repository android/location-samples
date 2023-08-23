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

package com.google.android.gms.location.sample.foregroundlocation.ui

import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private val locationPermissions =
    arrayOf(permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION)

internal fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

internal fun Activity.shouldShowRationaleFor(permission: String): Boolean =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

/**
 * State holder for location permissions. Properties are implemented as State objects so that they
 * trigger a recomposition when the value changes (if the value is read within a Composable scope).
 * This also implements the behavior for requesting location permissions and updating the internal
 * state afterward.
 *
 * This class should be initialized in `onCreate()` of the Activity. Sample usage:
 *
 * ```
 * override fun onCreate(savedInstanceState: Bundle?) {
 *     super.onCreate(savedInstanceState)
 *
 *     val locationPermissionState = LocationPermissionState(this) { permissionState ->
 *         if (permissionState.accessFineLocationGranted) {
 *             // Do something requiring precise location permission
 *         }
 *     }
 *
 *     setContent {
 *         Button(
 *             onClick = { locationPermissionState.requestPermissions() }
 *         ) {
 *             Text("Click Me")
 *         }
 *     }
 * }
 * ```
 */
class LocationPermissionState(
    private val activity: ComponentActivity,
    private val onResult: (LocationPermissionState) -> Unit
) {

    /** Whether permission was granted to access approximate location. */
    var accessCoarseLocationGranted by mutableStateOf(false)
        private set

    /** Whether to show a rationale for permission to access approximate location. */
    var accessCoarseLocationNeedsRationale by mutableStateOf(false)
        private set

    /** Whether permission was granted to access precise location. */
    var accessFineLocationGranted by mutableStateOf(false)
        private set

    /** Whether to show a rationale for permission to access precise location. */
    var accessFineLocationNeedsRationale by mutableStateOf(false)
        private set

    /**
     * Whether to show a degraded experience (set after the permission is denied).
     */
    var showDegradedExperience by mutableStateOf(false)
        private set

    private val permissionLauncher: ActivityResultLauncher<Array<String>> =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            updateState()
            showDegradedExperience = !hasPermission()
            onResult(this)
        }

    init {
        updateState()
    }

    private fun updateState() {
        accessCoarseLocationGranted = activity.hasPermission(permission.ACCESS_COARSE_LOCATION)
        accessCoarseLocationNeedsRationale =
            activity.shouldShowRationaleFor(permission.ACCESS_COARSE_LOCATION)
        accessFineLocationGranted = activity.hasPermission(permission.ACCESS_FINE_LOCATION)
        accessFineLocationNeedsRationale =
            activity.shouldShowRationaleFor(permission.ACCESS_FINE_LOCATION)
    }

    /**
     * Launch the permission request. Note that this may or may not show the permission UI if the
     * permission has already been granted or if the user has denied permission multiple times.
     */
    fun requestPermissions() {
        permissionLauncher.launch(locationPermissions)
    }

    fun hasPermission(): Boolean = accessCoarseLocationGranted || accessFineLocationGranted

    fun shouldShowRationale(): Boolean = !hasPermission() && (
        accessCoarseLocationNeedsRationale || accessFineLocationNeedsRationale)
}
