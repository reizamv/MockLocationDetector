package com.inforoeste.mocklocationdetector.sample

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationServices.FusedLocationApi
import com.inforoeste.mocklocationdetector.MockLocationDetector
import com.inforoeste.mocklocationdetector.MockLocationDetector.isAllowMockLocationsOn
import java.text.DateFormat
import java.util.*

open class MainActivity : AppCompatActivity(), ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mCurrentLocation: Location? = null
    private var mLastUpdateTime: String? = null
    private var mLatitudeTextView: TextView? = null
    private var mLongitudeTextView: TextView? = null
    private var mLastUpdateTimeTextView: TextView? = null
    private var mIsMockTextView: TextView? = null
    private var mAreMockLocationAppsPresentTextView: TextView? = null
    private var mIsMockLocationsOnTextView: TextView? = null
    private var mLocationRequest: LocationRequest? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mLatitudeTextView = findViewById<View>(R.id.txt_location_latitude) as TextView
        mLongitudeTextView = findViewById<View>(R.id.txt_location_longitude) as TextView
        mLastUpdateTimeTextView = findViewById<View>(R.id.txt_location_last_update_time) as TextView
        mIsMockTextView = findViewById<View>(R.id.txt_is_mock_text) as TextView
        mAreMockLocationAppsPresentTextView = findViewById<View>(R.id.txt_are_mock_location_apps_present) as TextView
        mIsMockLocationsOnTextView = findViewById<View>(R.id.txt_is_allow_mock_locations_on) as TextView

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build()
        }
    }

    override fun onStart() {
        mGoogleApiClient!!.connect()
        super.onStart()
    }

    override fun onStop() {
        stopLocationUpdates()
        mGoogleApiClient!!.disconnect()
        super.onStop()
    }

     private fun stopLocationUpdates() {
        FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this)
    }

    override fun onConnected(connectionHint: Bundle?) {
        createLocationRequest()
        startLocationUpdates()
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        //        mLocationRequest.setInterval(10000);
//        mLocationRequest.setFastestInterval(5000);
        mLocationRequest!!.interval = 2000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this)
    }

    override fun onLocationChanged(location: Location) {
        mCurrentLocation = location
        mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
        updateUI()
    }

    private fun updateUI() {
        mLatitudeTextView!!.text = mCurrentLocation!!.latitude.toString()
        mLongitudeTextView!!.text = mCurrentLocation!!.longitude.toString()
        mLastUpdateTimeTextView!!.text = mLastUpdateTime
        val isMock = MockLocationDetector.isLocationFromMockProvider(this, mCurrentLocation!!)
        mIsMockTextView!!.text = isMock.toString()
        if (isMock) {
            mIsMockTextView!!.setTextColor(ContextCompat.getColor(this, R.color.c_red))
        } else {
            mIsMockTextView!!.setTextColor(ContextCompat.getColor(this, R.color.c_green))
        }
        val mockLocationAppsPresent = MockLocationDetector.checkForAllowMockLocationsApps(this)
        mAreMockLocationAppsPresentTextView!!.text = mockLocationAppsPresent.toString()
        if (mockLocationAppsPresent) {
            mAreMockLocationAppsPresentTextView!!.setTextColor(ContextCompat.getColor(this, R.color.c_red))
        } else {
            mAreMockLocationAppsPresentTextView!!.setTextColor(ContextCompat.getColor(this, R.color.c_green))
        }
        val isAllowMockLocationsON = isAllowMockLocationsOn(this)
        mIsMockLocationsOnTextView!!.text = isAllowMockLocationsON.toString()
        if (isAllowMockLocationsON) {
            mIsMockLocationsOnTextView!!.setTextColor(ContextCompat.getColor(this, R.color.c_red))
        } else {
            mIsMockLocationsOnTextView!!.setTextColor(ContextCompat.getColor(this, R.color.c_green))
        }
    }
}