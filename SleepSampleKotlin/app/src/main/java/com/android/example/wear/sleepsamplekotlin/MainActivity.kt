/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.example.wear.sleepsamplekotlin

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.example.wear.sleepsamplekotlin.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

/**
 * Demos Android's Sleep APIs; subscribe/unsubscribe to sleep data, save that data, and display it.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onClickRequestSleepData(view: View) {
        if (activityRecognitionPermissionApproved()) {
            // TODO: Implement BroadcastReceiver for sleep events
            Log.d(TAG, "Permission already approved.")
        } else {
            requestPermissionLauncher.launch(permission.ACTIVITY_RECOGNITION)
        }
    }

    private fun activityRecognitionPermissionApproved(): Boolean {
        // Because this app targets 29 and above (recommendation for using the Sleep APIs), we
        // don't need to check if this is on a device before runtime permissions, that is, a device
        // prior to 29 / Q.
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            this,
            permission.ACTIVITY_RECOGNITION
        )
    }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                // Permission denied on Android platform that supports runtime permissions.
                displayPermissionSettingsSnackBar()
            } else {
                // Permission was granted (either by approval or Android version below Q).
                binding.outputTextView.text = getString(R.string.permission_approved)
                // TODO: Track sleep
            }
        }

    private fun displayPermissionSettingsSnackBar() {
        Snackbar.make(
            binding.mainActivity,
            R.string.permission_rational,
            Snackbar.LENGTH_LONG
        )
            .setAction(R.string.action_settings) {
                // Build intent that displays the App settings screen.
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(
                    "package",
                    BuildConfig.APPLICATION_ID,
                    null
                )
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            .show()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
