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
import com.google.android.gms.location.sample.locationaddress.UiState.Initializing
import com.google.android.gms.location.sample.locationaddress.UiState.PlayServicesAvailable
import com.google.android.gms.location.sample.locationaddress.UiState.PlayServicesUnavailable
import com.google.android.gms.location.sample.locationaddress.ui.GeocoderScreen
import com.google.android.gms.location.sample.locationaddress.ui.InitializingScreen
import com.google.android.gms.location.sample.locationaddress.ui.LocationPermissionState
import com.google.android.gms.location.sample.locationaddress.ui.ServiceUnavailableScreen
import com.google.android.gms.location.sample.locationaddress.ui.theme.LocationAddressTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationPermissionState = LocationPermissionState(this) {
            if (it.accessFineLocationGranted) {
                viewModel.getCurrentAddress()
            }
        }

        setContent {
            LocationAddressTheme {
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
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    locationPermissionState: LocationPermissionState
) {
    val uiState by viewModel.uiState.collectAsState()
    when (uiState) {
        Initializing -> InitializingScreen()
        PlayServicesUnavailable -> ServiceUnavailableScreen()
        PlayServicesAvailable -> GeocoderScreen(
            showRationale = locationPermissionState.shouldShowRationale(),
            showProgress = viewModel.showProgress,
            addresses = viewModel.addressList,
            maxResults = viewModel.maxResults,
            maxResultsRange = viewModel.maxResultsRange,
            onMaxResultsChange = viewModel::updateMaxResults,
            onFindAddressClick = { locationPermissionState.requestPermissions() }
        )
    }
}
