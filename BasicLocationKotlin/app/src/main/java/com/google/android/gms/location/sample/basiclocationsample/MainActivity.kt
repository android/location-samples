/*
  Copyright 2017 Google Inc. All Rights Reserved.
  <p>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p>
  http://www.apache.org/licenses/LICENSE-2.0
  <p>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package com.google.android.gms.location.sample.basiclocationsample

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewbinding.BuildConfig
import com.google.android.gms.location.*
import com.google.android.gms.location.sample.basiclocationsample.databinding.MainActivityBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE

/**
 * Demonstrates use of the Location API to retrieve the last known location for a device.
 */
class MainActivity: AppCompatActivity() {
    private val TAG = "MainActivity"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    /**
    * ViewBinding
     */
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ViewBinding initialization
        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }
    
    override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }
    }
    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions() = ActivityCompat.checkSelfPermission(this,
        ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)
            && ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackbar(R.string.permission_rationale, android.R.string.ok) {
                // Request permission
                startLocationPermissionRequest()
            }
            
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }
    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION), REQUEST_PERMISSIONS_REQUEST_CODE)
    }
    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                grantResults.isEmpty() -> Log.i(TAG, "User interaction was cancelled.")
                
                // Permission granted.
                (grantResults[0] == PERMISSION_GRANTED) -> getLastLocation()
                
                // Permission denied.
                
                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.
                
                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                else -> {
                    showSnackbar(R.string.permission_denied_explanation, R.string.settings) {
                        // Build intent that displays the App settings screen.
                        val intent = Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package",
                                BuildConfig.LIBRARY_PACKAGE_NAME, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }
    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     *
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        Log.d(TAG, "getLastLocation")
        fusedLocationClient.lastLocation.addOnCompleteListener { taskLocation ->
            if (taskLocation.isSuccessful && taskLocation.result != null) {
                updateViews(taskLocation.result)
            } else {
                requestNewLocationData()
                /*Log.w(TAG, "getLastLocation:exception", taskLocation.exception)
                showSnackbar(R.string.no_location_detected)*/
            }
        }
    }
    fun updateViews(currentLocation: Location) {
        Log.d(TAG, "updateViews")
        binding.currentLatitude.text = resources
            .getString(R.string.latitude_label, currentLocation.latitude)
        binding.currentLongitude.text = resources
            .getString(R.string.longitude_label, currentLocation.longitude)
    }
    fun requestNewLocationData() {
        Log.d(TAG, "requestNewLocationData")
        // Initializing LocationRequest
        // object with appropriate methods
        val locationRequest = LocationRequest().apply {
            // For a high level accuracy use PRIORITY_HIGH_ACCURACY argument.
            // For a low level accuracy (city), use PRIORITY_LOW_POWER
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 3
            fastestInterval = 1
            numUpdates = 2
        }
        
        // setting LocationRequest on a FusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_DENIED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission_group.LOCATION), PERMISSIONS_ALLOW_USING_LOCATION_ID
            )
        } else {
            locationClient?.requestLocationUpdates(locationRequest,
                locationCallback, Looper.myLooper())
        }*/
    
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
            == PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.myLooper())
        }
    }
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Log.d(TAG, "onLocationResult")
            updateViews(locationResult.lastLocation)
        }
    }
    /**
     * Shows a [Snackbar].
     *
     * @param snackStrId The id for the string resource for the Snackbar text.
     * @param actionStrId The text of the action item.
     * @param listener The listener associated with the Snackbar action.
     */
    private fun showSnackbar(snackStrId: Int, actionStrId: Int = 0,
                             listener: View.OnClickListener? = null) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), getString(snackStrId),
                LENGTH_INDEFINITE)
        if (actionStrId != 0 && listener != null) {
            snackBar.setAction(getString(actionStrId), listener)
        }
        snackBar.show()
    }
}
