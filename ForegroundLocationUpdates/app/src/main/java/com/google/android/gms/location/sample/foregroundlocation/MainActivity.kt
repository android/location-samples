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

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.google.android.gms.location.sample.foregroundlocation.PlayServicesAvailableState.Initializing
import com.google.android.gms.location.sample.foregroundlocation.PlayServicesAvailableState.PlayServicesAvailable
import com.google.android.gms.location.sample.foregroundlocation.PlayServicesAvailableState.PlayServicesUnavailable
import com.google.android.gms.location.sample.foregroundlocation.ui.InitializingScreen
import com.google.android.gms.location.sample.foregroundlocation.ui.LocationPermissionState
import com.google.android.gms.location.sample.foregroundlocation.ui.LocationUpdatesScreen
import com.google.android.gms.location.sample.foregroundlocation.ui.ServiceUnavailableScreen
import com.google.android.gms.location.sample.foregroundlocation.ui.theme.ForegroundLocationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationPermissionState = LocationPermissionState(this) {
            if (it.hasPermission()) {
                viewModel.toggleLocationUpdates()
            }
        }

        setContent {
            ForegroundLocationTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(stringResource(id = R.string.app_name))
                            }
                        )
                    }
                ) {
                    MainScreen(
                        viewModel = viewModel,
                        locationPermissionState = locationPermissionState
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, ForegroundLocationService::class.java)
        bindService(serviceIntent, viewModel, BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unbindService(viewModel)
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    locationPermissionState: LocationPermissionState
) {
    val uiState by viewModel.playServicesAvailableState.collectAsState()
    val isLocationOn by viewModel.isReceivingLocationUpdates.collectAsState()
    val lastLocation by viewModel.lastLocation.collectAsState()

    when (uiState) {
        Initializing -> InitializingScreen()
        PlayServicesUnavailable -> ServiceUnavailableScreen()
        PlayServicesAvailable -> {
            LocationUpdatesScreen(
                showDegradedExperience = locationPermissionState.showDegradedExperience,
                needsPermissionRationale = locationPermissionState.shouldShowRationale(),
                onButtonClick = locationPermissionState::requestPermissions,
                isLocationOn = isLocationOn,
                location = lastLocation,
            )
        }
    }
}
