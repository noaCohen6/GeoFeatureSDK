package com.example.geofeaturelibrary

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.Locale

/**
 * Location manager for detecting user's country from GPS coordinates
 */
class GeoLocationManager(private val context: Context) {

    companion object {
        private const val TAG = "GeoLocationManager"
    }


    fun getCountryFromLocation(callback: (String?) -> Unit) {
        // Check permission
        if (!hasLocationPermission()) {
            Log.w(TAG, "Location permission not granted")
            callback(null)
            return
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            val location = getLastKnownLocation(locationManager)

            if (location != null) {
                // Convert GPS coordinates to country code
                val countryCode = getCountryCodeFromCoordinates(location.latitude, location.longitude)
                callback(countryCode)
            } else {
                Log.w(TAG, "No location available")
                callback(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting location", e)
            callback(null)
        }
    }

    //Check if location permission is granted
    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }


    private fun getLastKnownLocation(locationManager: LocationManager): Location? {
        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (gpsLocation != null) {
                    Log.d(TAG, "Got location from GPS: ${gpsLocation.latitude}, ${gpsLocation.longitude}")
                    return gpsLocation
                }
            }

            // Fallback to Network provider
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (networkLocation != null) {
                    Log.d(TAG, "Got location from Network: ${networkLocation.latitude}, ${networkLocation.longitude}")
                    return networkLocation
                }
            }

            Log.w(TAG, "No last known location available from any provider")
            return null
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception getting location", e)
            return null
        }
    }


    private fun getCountryCodeFromCoordinates(latitude: Double, longitude: Double): String? {
        if (!Geocoder.isPresent()) {
            Log.w(TAG, "Geocoder not available on this device")
            return null
        }

        return try {
            val geocoder = Geocoder(context, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ (API 33+)
                var countryCode: String? = null
                val addresses = mutableListOf<android.location.Address>()

                // This is synchronous in practice despite the callback
                geocoder.getFromLocation(latitude, longitude, 1) { results ->
                    addresses.addAll(results)
                }

                // Give it a moment to complete
                Thread.sleep(100)

                if (addresses.isNotEmpty()) {
                    countryCode = addresses[0].countryCode
                    Log.d(TAG, "Geocoded to country: $countryCode")
                }
                countryCode
            } else {
                // Android 12 and below
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                val countryCode = addresses?.firstOrNull()?.countryCode
                if (countryCode != null) {
                    Log.d(TAG, "Geocoded to country: $countryCode")
                }
                countryCode
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error geocoding coordinates ($latitude, $longitude)", e)
            null
        }
    }
}