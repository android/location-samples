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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.sample.locationaddress.UiState.Initializing
import com.google.android.gms.location.sample.locationaddress.UiState.PlayServicesAvailable
import com.google.android.gms.location.sample.locationaddress.UiState.PlayServicesUnavailable
import com.google.android.gms.location.sample.locationaddress.data.FormattedAddress
import com.google.android.gms.location.sample.locationaddress.ui.LocationPermissionState
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
        PlayServicesAvailable -> ServiceAvailable(viewModel, locationPermissionState)
    }
}

@Composable
fun InitializingScreen() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.initializing),
            style = MaterialTheme.typography.h6
        )
        CircularProgressIndicator()
    }
}

@Composable
fun ServiceUnavailableScreen() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.play_services_unavailable),
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Icon(
            Icons.Filled.Warning,
            tint = MaterialTheme.colors.primary,
            contentDescription = "",
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
fun ServiceAvailable(viewModel: MainViewModel, locationPermissionState: LocationPermissionState) {
    /*
    * On Android 12 and later, the user could choose to grant access only to approximate location.
    * The user could also deny both permissions, and the system may tell us to show a rationale for
    * either or both permissions.
    * Since we require precise location, we'll show the rationale if the system tells us to show one
    * for either permission, and we'll also show it if permission was requested but precise location
    * was not granted.
    */
    val showRationale = locationPermissionState.accessCoarseLocationNeedsRationale ||
        locationPermissionState.accessFineLocationNeedsRationale ||
        (locationPermissionState.permissionRequested &&
            !locationPermissionState.accessFineLocationGranted)

    GeocoderScreen(showRationale, viewModel.showProgress, viewModel.addressList) {
        locationPermissionState.requestPermissions()
    }
}

@Composable
fun GeocoderScreen(
    showRationale: Boolean,
    showProgress: Boolean,
    addresses: List<FormattedAddress>,
    onButtonClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        GeocoderControls(
            showRationale = showRationale,
            buttonEnabled = !showProgress,
            onButtonClick = onButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .padding(all = 16.dp)
        )
        GeocoderResults(
            showProgress = showProgress,
            addresses = addresses,
            modifier = Modifier.fillMaxHeight(0.7f)
        )
    }
}

@Composable
fun GeocoderControls(
    modifier: Modifier = Modifier,
    showRationale: Boolean,
    buttonEnabled: Boolean,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            enabled = buttonEnabled,
            onClick = onButtonClick
        ) {
            Text(text = stringResource(id = R.string.find_address))
        }
        if (showRationale) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Warning,
                    tint = Color.Red,
                    contentDescription = "",
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = stringResource(id = R.string.location_permissions_rationale),
                    color = Color.Red,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

@Composable
fun GeocoderResults(
    showProgress: Boolean,
    addresses: List<FormattedAddress>,
    modifier: Modifier = Modifier
) {
    val resultText = if (showProgress) {
        stringResource(id = R.string.fetching_address)
    } else {
        stringResource(id = R.string.num_results, addresses.size)
    }

    Column(modifier = modifier) {
        Text(
            text = resultText,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colors.primary)
        )

        if (showProgress) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(all = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(addresses) {
                    Text(
                        text = it.display,
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InitializingScreenPreview() {
    LocationAddressTheme {
        InitializingScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceUnavailableScreenPreview() {
    LocationAddressTheme {
        ServiceUnavailableScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun GeocoderScreenPreview() {
    val testData = listOf(
        FormattedAddress("555 Main St\nAnytown, NY 98765"),
        FormattedAddress("MetaCortex Bldg\n1337 Code Way\nThe Simulation, 0x00101010"),
        FormattedAddress("Behind the Curtain\nEmerald City\nLand of Oz\nSomewhere over the Rainbow\nNOT Kansas")
    )
    LocationAddressTheme {
        GeocoderScreen(
            showRationale = true,
            showProgress = false,
            addresses = testData
        )
    }
}
