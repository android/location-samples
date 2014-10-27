/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.location.sample.locationaddress;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Getting the Location Address.
 *
 * Demonstrates how to use the {@link android.location.Geocoder} API and reverse geocoding to
 * display a device's location as an address.
 *
 * For an example that displays the last known location of a device using a longitude and latitude,
 * see https://github.com/googlesamples/android-play-location/tree/master/BasicLocation.
 *
 * For an example that shows location updates using the Fused Location Provider API, see
 * https://github.com/googlesamples/android-play-location/tree/master/LocationUpdates.
 *
 * This sample uses Google Play services (GoogleApiClient) but does not need to authenticate a user.
 * For an example that uses authentication, see
 * https://github.com/googlesamples/android-google-accounts/tree/master/QuickStart.
 */
public class MainActivity extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    protected static final String TAG = "location-address-sample";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    /*
     * UI widget that displays the location address.
     */
    protected TextView mLocationAddress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mLocationAddress = (TextView) findViewById(R.id.location_address);
        buildGoogleApiClient();
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        makeLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     */
    protected void makeLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                return;
            }

            // Start the background task for fetching the address.
            Toast.makeText(this, R.string.getting_address, Toast.LENGTH_SHORT).show();

            (new MainActivity.GetAddressTask(this)).execute(mLastLocation);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /*
    * Called by Google Play services if the connection to GoogleApiClient drops because of an
    * error.
    */
    public void onDisconnected() {
        Log.i(TAG, "Disconnected");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * An AsyncTask that calls {@code Geocoder.getFromLocation()} in the background.
     *
     * The class uses the following generic types:
     * Location - A {@link android.location.Location} object containing the last known location,
     *            passed as the input parameter to {@code doInBackground()}.
     * Void     - Indicates that progress units are not used by this subclass.
     * String   - An address passed to {@code onPostExecute()}.
     */
    protected class GetAddressTask extends AsyncTask<Location, Void, String> {
        Context context;

        public GetAddressTask(Context context) {
            super();
            this.context = context;
        }

        /**
         * Gets the location address using reverse geocoding. Returns the formatted address to
         * the UI thread.
         */
        @Override
        protected String doInBackground(Location... params) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());

            // Gets the current location from the input parameter list.
            Location location = params[0];

            List<Address> addresses = null;

            try {
                 // Using getFromLocation() returns the current location's latitude and longitude
                 // values. In this case, gets a single address.
                addresses = geocoder.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
            } catch (IOException ioException) {
                // Catch network or other I/O problems.
                Log.i(TAG, "Cannot fetch address. It appears that the network is unavailable.",
                        ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch incorrect latitude or longitude values.
                Log.i(TAG, "Error: Invalid latitude or longitude used. " +
                "Latitude = " + location.getLatitude() +
                ", Longitude = " + location.getLongitude(), illegalArgumentException);
            }

            // Check if reverse geocode returned an address.
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                // We fetch the address lines using {@code getAddressLine},
                // join them, and send them to the thread. The {@link android.location.address}
                // class provides other options for fetching address details that you may prefer
                // to use. Here are some examples:
                // getLocality() ("Mountain View", for example)
                // getAdminArea() ("CA", for example)
                // getPostalCode() ("94043", for example)
                // getCountryCode() ("US", for example)
                // getCountryName() ("United States", for example)
                for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
                return TextUtils.join(System.getProperty("line.separator"), addressFragments);
            } else {
                return "No address found";
            }
        }

        /**
         * Runs once doInBackground() completes. Sets the text of the UI element that displays the
         * address. Runs on the UI thread.
         *
         * @param address The String returned by doInBackground().
         */
        @Override
        protected void onPostExecute(String address) {
            // Show address in the UI.
            mLocationAddress.setText(address);
        }
    }
}