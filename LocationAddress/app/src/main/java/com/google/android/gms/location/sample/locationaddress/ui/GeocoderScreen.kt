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

package com.google.android.gms.location.sample.locationaddress.ui

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.android.gms.location.sample.locationaddress.R
import com.google.android.gms.location.sample.locationaddress.data.FormattedAddress
import com.google.android.gms.location.sample.locationaddress.ui.theme.LocationAddressTheme

@Composable
fun GeocoderScreen(
    showRationale: Boolean,
    showProgress: Boolean,
    addresses: List<FormattedAddress>,
    maxResults: Int,
    maxResultsRange: IntRange,
    onMaxResultsChange: (Int) -> Unit,
    onFindAddressClick: () -> Unit
) {
    val orientation = LocalConfiguration.current.orientation
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        Column {
            GeocoderControls(
                showRationale = showRationale,
                maxResults = maxResults,
                maxResultsRange = maxResultsRange,
                onMaxResultsChange = onMaxResultsChange,
                findAddressEnabled = !showProgress,
                onFindAddressClick = onFindAddressClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.4f)
                    .padding(all = 16.dp)
            )
            GeocoderResults(
                showProgress = showProgress,
                addresses = addresses,
                modifier = Modifier.weight(.6f)
            )
        }
    } else {
        Row {
            GeocoderControls(
                showRationale = showRationale,
                maxResults = maxResults,
                maxResultsRange = maxResultsRange,
                onMaxResultsChange = onMaxResultsChange,
                findAddressEnabled = !showProgress,
                onFindAddressClick = onFindAddressClick,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(.5f)
                    .padding(all = 16.dp)
            )
            GeocoderResults(
                showProgress = showProgress,
                addresses = addresses,
                modifier = Modifier.weight(.5f)
            )
        }
    }
}

@Composable
fun GeocoderControls(
    modifier: Modifier = Modifier,
    showRationale: Boolean,
    maxResults: Int,
    maxResultsRange: IntRange,
    onMaxResultsChange: (Int) -> Unit,
    findAddressEnabled: Boolean,
    onFindAddressClick: () -> Unit
) {
    ConstraintLayout(modifier = modifier) {
        val (button, rationale, maxResultControls) = createRefs()
        Button(
            enabled = findAddressEnabled,
            onClick = onFindAddressClick,
            modifier = Modifier.constrainAs(button) {
                centerTo(parent)
            }
        ) {
            Text(text = stringResource(id = R.string.find_address))
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.constrainAs(maxResultControls) {
                centerHorizontallyTo(parent)
                bottom.linkTo(button.top, margin = 16.dp)
            }
        ) {
            Text(text = stringResource(R.string.max_results, maxResults))
            DiscreteSlider(
                value = maxResults,
                valueRange = maxResultsRange,
                onValueChange = onMaxResultsChange,
                modifier = Modifier.width(240.dp)
            )
        }
        if (showRationale) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.constrainAs(rationale) {
                    centerHorizontallyTo(parent)
                    top.linkTo(button.bottom, margin = 16.dp)
                }
            ) {
                Icon(
                    Icons.Filled.Warning,
                    tint = Color.Red,
                    contentDescription = null,
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
fun GeocoderScreenPreview() {
    LocationAddressTheme {
        GeocoderScreen(
            showRationale = true,
            showProgress = false,
            addresses = previewData,
            maxResults = 5,
            maxResultsRange = 1..5,
            onMaxResultsChange = {},
            onFindAddressClick = {}
        )
    }
}

private val previewData = listOf(
    FormattedAddress("Googleplex, 1600 Amphitheatre Pkwy, Mountain View, CA 94043"),
    FormattedAddress("San Francisco International Airport, San Francisco, CA 94128"),
    FormattedAddress("123 Elf Road, North Pole, 88888")
)
