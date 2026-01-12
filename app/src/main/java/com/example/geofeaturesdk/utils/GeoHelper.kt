package com.example.geofeaturesdk.utils

import android.content.Context
import com.example.geofeaturelibrary.GeoFeatureSDK

/**
 * Helper class for GeoFeature SDK in demo app
 * Provides a wrapper around getCurrentCountry that respects manual override
 * even when GPS is available
 */
object GeoHelper {


    fun getCurrentCountry(context: Context, callback: (String) -> Unit) {
        // Check if user has manually selected a country
        val manualCountry = GeoFeatureSDK.getUserCountry(context)

        if (manualCountry != null) {
            // User chose a country manually - use it directly!
            callback(manualCountry)
        } else {
            // No manual selection - use SDK's normal flow (GPS → Locale)
            GeoFeatureSDK.getCurrentCountry(context, callback)
        }
    }


    fun isFeatureEnabled(
        context: Context,
        featureName: String,
        callback: (enabled: Boolean, value: String?) -> Unit
    ) {
        getCurrentCountry(context) { countryCode ->
            GeoFeatureSDK.isFeatureEnabledForCountry(featureName, countryCode, callback)
        }
    }


}