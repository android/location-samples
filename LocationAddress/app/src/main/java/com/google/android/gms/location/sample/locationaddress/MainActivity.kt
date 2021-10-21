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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.sample.locationaddress.UiState.Initializing
import com.google.android.gms.location.sample.locationaddress.UiState.PlayServicesAvailable
import com.google.android.gms.location.sample.locationaddress.UiState.PlayServicesUnavailable
import com.google.android.gms.location.sample.locationaddress.ui.theme.LocationAddressTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    when (uiState) {
        Initializing -> InitializingScreen()
        PlayServicesUnavailable -> ServiceUnavailableScreen()
        PlayServicesAvailable -> GeocoderScreen()
    }
}

@Composable
fun InitializingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.initializing),
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(all = 16.dp)
                .align(Alignment.CenterHorizontally)
        )
        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun ServiceUnavailableScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.play_services_unavailable),
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(all = 16.dp)
                .align(Alignment.CenterHorizontally),
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
fun GeocoderScreen() {
    Button(
        modifier = Modifier.padding(all = 16.dp),
        onClick = { /*TODO*/ }
    ) {
        Text(text = "Find Address")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InitializingScreenPreview() {
    LocationAddressTheme {
        InitializingScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ServiceUnavailableScreenPreview() {
    LocationAddressTheme {
        ServiceUnavailableScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun GeocoderScreenPreview() {
    LocationAddressTheme {
        GeocoderScreen()
    }
}
