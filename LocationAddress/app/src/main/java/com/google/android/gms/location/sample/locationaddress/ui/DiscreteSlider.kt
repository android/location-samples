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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.Slider
import androidx.compose.material.SliderColors
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlin.math.roundToInt

/**
 * Compose [Slider] works with floating point values. This version works with integers and hides the
 * details of using floats underneath.
 */
@Composable
fun DiscreteSlider(
    value: Int,
    valueRange: ClosedRange<Int>,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SliderColors = SliderDefaults.colors()
) = Slider(
    value = value.toFloat(),
    onValueChange = { onValueChange(it.roundToInt()) },
    modifier = modifier,
    enabled = enabled,
    valueRange = valueRange.start.toFloat()..valueRange.endInclusive.toFloat(),
    steps = valueRange.endInclusive - valueRange.start - 1,
    onValueChangeFinished = onValueChangeFinished,
    interactionSource = interactionSource,
    colors = colors
)
