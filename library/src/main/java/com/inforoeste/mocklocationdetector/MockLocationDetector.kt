package com.inforoeste.mocklocationdetector

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.provider.Settings
import android.util.Log

/**
 * Created by smarques84 on 15/05/16.
 */
object MockLocationDetector {
    private val TAG = MockLocationDetector::class.java.simpleName

    //Always return false on Marshmallow because the settings have been updated and its not possible to check if an app has been allowed
    // to perform mock locations. Please use isLocationFromMockProvider instead
    @Deprecated("")
    fun isAllowMockLocationsOn(context: Context): Boolean {
        // returns true if mock location enabled, false if not enabled.
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Settings.Secure.getString(context.contentResolver,
                    Settings.Secure.ALLOW_MOCK_LOCATION) != "0"
        } else {
            false
        }
    }

    fun checkForAllowMockLocationsApps(context: Context): Boolean {
        var count = 0
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (applicationInfo in packages) {
            try {
                val packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS)

                // Get Permissions
                val requestedPermissions = packageInfo.requestedPermissions
                if (requestedPermissions != null) {
                    for (i in requestedPermissions.indices) {
                        if ((requestedPermissions[i]
                                        == "android.permission.ACCESS_MOCK_LOCATION") && applicationInfo.packageName != context.packageName) {
                            count++
                        }
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(TAG, "Got exception " + e.message)
            }
        }
        return count > 0
    }

    //http://stackoverflow.com/questions/6880232/disable-check-for-mock-location-prevent-gps-spoofing
    fun isLocationFromMockProvider(context: Context, location: Location): Boolean {
        var isMock = false
        isMock = if (Build.VERSION.SDK_INT >= 18) {
            location.isFromMockProvider
        } else {
            //isMock = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
            return Settings.Secure.getString(context.contentResolver,
                    Settings.Secure.ALLOW_MOCK_LOCATION) != "0"
        }
        return isMock
    }
}