/*
 * Copyright 2018 Google LLC
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

package com.google.android.gms.location.sample.locationaddress

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.provider.Settings
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener

/**
 * Getting the Location Address.
 *
 * Demonstrates how to use the [android.location.Geocoder] API and reverse geocoding to
 * display a device's location as an address. Uses an IntentService to fetch the location address,
 * and a ResultReceiver to process results sent by the IntentService.
 *
 * Android has two location request settings:
 * `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION`. These settings control
 * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
 * the AndroidManifest.xml.
 *
 * For a starter example that displays the last known location of a device using a longitude and latitude,
 * see https://github.com/googlesamples/android-play-location/tree/master/BasicLocation.
 *
 * For an example that shows location updates using the Fused Location Provider API, see
 * https://github.com/googlesamples/android-play-location/tree/master/LocationUpdates.
 */
class MainActivity : AppCompatActivity() {


    private val TAG = MainActivity::class.java.simpleName

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private val ADDRESS_REQUESTED_KEY = "address-request-pending"
    private val LOCATION_ADDRESS_KEY = "location-address"

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var fusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Represents a geographical location.
     */
    private var lastLocation: Location? = null

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     */
    private var addressRequested = false

    /**
     * The formatted location address.
     */
    private var addressOutput = ""

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private lateinit var resultReceiver: AddressResultReceiver

    /**
     * Displays the location address.
     */
    private lateinit var locationAddressTextView: TextView

    /**
     * Visible while the address is being fetched.
     */
    private lateinit var progressBar: ProgressBar

    /**
     * Kicks off the request to fetch an address when pressed.
     */
    private lateinit var fetchAddressButton: Button

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        resultReceiver = AddressResultReceiver(Handler())

        locationAddressTextView = findViewById(R.id.location_address_view)
        progressBar = findViewById(R.id.progress_bar)
        fetchAddressButton = findViewById(R.id.fetch_address_button)

        // Set defaults, then update using values stored in the Bundle.
        addressRequested = false
        addressOutput = ""
        updateValuesFromBundle(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        updateUIWidgets()
    }

    public override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getAddress()
        }
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        ADDRESS_REQUESTED_KEY.let {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(it)) {
                addressRequested = savedInstanceState.getBoolean(it)
            }
        }

        LOCATION_ADDRESS_KEY.let {
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(it)) {
                addressOutput = savedInstanceState.getString(it)
                displayAddressOutput()
            }
        }


    }

    /**
     * Runs when user clicks the Fetch Address button.
     */
    @Suppress("UNUSED_PARAMETER")
    fun fetchAddressButtonHandler(view: View) {
        if (lastLocation != null) {
            startIntentService()
            return
        }

        // If we have not yet retrieved the user location, we process the user's request by setting
        // addressRequested to true. As far as the user is concerned, pressing the Fetch Address
        // button immediately kicks off the process of getting the address.
        addressRequested = true
        updateUIWidgets()
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private fun startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        val intent = Intent(this, FetchAddressIntentService::class.java).apply {
            // Pass the result receiver as an extra to the service.
            putExtra(Constants.RECEIVER, resultReceiver)

            // Pass the location data as an extra to the service.
            putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation)
        }

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent)
    }

    /**
     * Gets the address for the last known location.
     */
    @SuppressLint("MissingPermission")
    private fun getAddress() {
        fusedLocationClient?.lastLocation?.addOnSuccessListener(this, OnSuccessListener { location ->
            if (location == null) {
                Log.w(TAG, "onSuccess:null")
                return@OnSuccessListener
            }

            lastLocation = location

            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Snackbar.make(findViewById<View>(android.R.id.content),
                        R.string.no_geocoder_available, Snackbar.LENGTH_LONG).show()
                return@OnSuccessListener
            }

            // If the user pressed the fetch address button before we had the location,
            // this will be set to true indicating that we should kick off the intent
            // service after fetching the location.
            if (addressRequested) startIntentService()
        })?.addOnFailureListener(this) { e -> Log.w(TAG, "getLastLocation:onFailure", e) }
    }

    /**
     * Updates the address in the UI.
     */
    private fun displayAddressOutput() {
        locationAddressTextView.text = addressOutput
    }

    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
    private fun updateUIWidgets() {
        if (addressRequested) {
            progressBar.visibility = ProgressBar.VISIBLE
            fetchAddressButton.isEnabled = false
        } else {
            progressBar.visibility = ProgressBar.GONE
            fetchAddressButton.isEnabled = true
        }
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        with(savedInstanceState) {
            // Save whether the address has been requested.
            putBoolean(ADDRESS_REQUESTED_KEY, addressRequested)

            // Save the address string.
            putString(LOCATION_ADDRESS_KEY, addressOutput)
        }

        super.onSaveInstanceState(savedInstanceState)
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    private inner class AddressResultReceiver internal constructor(
            handler: Handler
    ) : ResultReceiver(handler) {

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {

            // Display the address string or an error message sent from the intent service.
            addressOutput = resultData.getString(Constants.RESULT_DATA_KEY)
            displayAddressOutput()

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText(this@MainActivity, R.string.address_found, Toast.LENGTH_SHORT).show()
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            addressRequested = false
            updateUIWidgets()
        }
    }

    /**
     * Shows a [Snackbar].
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private fun showSnackbar(
            mainTextStringId: Int,
            actionStringId: Int,
            listener: View.OnClickListener
    ) {
        Snackbar.make(findViewById(android.R.id.content), getString(mainTextStringId),
                    Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener)
                .show()
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    View.OnClickListener {
                        // Request permission
                        ActivityCompat.requestPermissions(this@MainActivity,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                REQUEST_PERMISSIONS_REQUEST_CODE)
                    })

        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
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

        if (requestCode != REQUEST_PERMISSIONS_REQUEST_CODE) return

        when {
            grantResults.isEmpty() ->
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            grantResults[0] == PackageManager.PERMISSION_GRANTED -> // Permission granted.
                getAddress()
            else -> // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.

                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        View.OnClickListener {
                            // Build intent that displays the App settings screen.
                            val intent = Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                        })
        }

    }

}
