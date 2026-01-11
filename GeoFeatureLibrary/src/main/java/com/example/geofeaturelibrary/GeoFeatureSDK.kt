package com.example.geofeaturelibrary

import android.content.Context
import android.util.Log
import java.util.Locale

//  Main SDK class for GeoFeature functionality with GPS location support

object GeoFeatureSDK {

    private const val TAG = "GeoFeatureSDK"
    private const val DEFAULT_BASE_URL = "http://localhost:8080/"

    private lateinit var controller: GeoFeatureController
    private lateinit var locationManager: GeoLocationManager
    private var baseUrl: String = DEFAULT_BASE_URL
    private var isLocationInitialized = false


    fun initialize(baseUrl: String, context: Context) {
        this.baseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        controller = GeoFeatureController(this.baseUrl)
        locationManager = GeoLocationManager(context.applicationContext)
        isLocationInitialized = true
        Log.d(TAG, "GeoFeatureSDK initialized with URL: ${this.baseUrl}")
    }

    //Initialize without context (location features will be limited to Locale only)

    fun initialize(baseUrl: String) {
        this.baseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        controller = GeoFeatureController(this.baseUrl)
        isLocationInitialized = false
        Log.d(TAG, "GeoFeatureSDK initialized (without location) with URL: ${this.baseUrl}")
        Log.w(TAG, "Location features will use Locale only. Initialize with Context for GPS support.")
    }


    private fun getController(): GeoFeatureController {
        if (!::controller.isInitialized) {
            Log.w(TAG, "Controller not initialized, using default URL: $DEFAULT_BASE_URL")
            controller = GeoFeatureController(DEFAULT_BASE_URL)
        }
        return controller
    }


     // Get device country code from locale (fallback method)

    private fun getDeviceCountryCode(context: Context): String {
        return try {
            val locale = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                // Android 7.0+ (API 24+)
                context.resources.configuration.locales[0]
            } else {
                @Suppress("DEPRECATION")
                context.resources.configuration.locale
            }
            locale.country.uppercase()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting country code from locale", e)
            Locale.getDefault().country.uppercase()
        }
    }



     // Get current country code using GPS location (with Locale fallback)

    fun getCurrentCountry(context: Context, callback: (String) -> Unit) {
        // 1. Try GPS first if available
        if (isLocationInitialized && locationManager.hasLocationPermission()) {
            locationManager.getCountryFromLocation { gpsCountry ->
                if (gpsCountry != null) {
                    Log.d(TAG, "Country from GPS: $gpsCountry")
                    callback(gpsCountry)
                } else {
                    // GPS failed, check manual override
                    val userCountry = getUserCountry(context)
                    if (userCountry != null) {
                        Log.d(TAG, "GPS failed, using manual override: $userCountry")
                        callback(userCountry)
                    } else {
                        // No manual override, fallback to Locale
                        val localeCountry = getDeviceCountryCode(context)
                        Log.d(TAG, "GPS failed, no manual override, using Locale: $localeCountry")
                        callback(localeCountry)
                    }
                }
            }
        } else {
            // 2. No GPS permission, check manual override
            val userCountry = getUserCountry(context)
            if (userCountry != null) {
                Log.d(TAG, "No GPS permission, using manual override: $userCountry")
                callback(userCountry)
            } else {
                // 3. No manual override, use Locale
                val localeCountry = getDeviceCountryCode(context)
                if (!isLocationInitialized) {
                    Log.d(TAG, "Location not initialized, using Locale: $localeCountry")
                } else {
                    Log.d(TAG, "No location permission and no manual override, using Locale: $localeCountry")
                }
                callback(localeCountry)
            }
        }
    }


      //Check if user is currently in a specific country

    fun isInCountry(context: Context, countryCode: String, callback: (Boolean) -> Unit) {
        getCurrentCountry(context) { currentCountry ->
            val isInCountry = currentCountry.equals(countryCode, ignoreCase = true)
            Log.d(TAG, "Is in $countryCode? $isInCountry (current: $currentCountry)")
            callback(isInCountry)
        }
    }


     //Check if location permission is granted

    fun hasLocationPermission(context: Context): Boolean {
        return if (isLocationInitialized) {
            locationManager.hasLocationPermission()
        } else {
            false
        }
    }

    // ==================== MANUAL COUNTRY OVERRIDE ====================


     // Manually set the user's country (overrides GPS and Locale)
     // Useful for testing or when user wants to select their country

    fun setUserCountry(context: Context, countryCode: String) {
        context.getSharedPreferences("geo_feature_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("user_country", countryCode.uppercase())
            .apply()
        Log.d(TAG, "User country manually set to: $countryCode")
    }


     // Get manually set country (if any)

    fun getUserCountry(context: Context): String? {
        return context.getSharedPreferences("geo_feature_prefs", Context.MODE_PRIVATE)
            .getString("user_country", null)
    }


     // Clear manual country override (will use GPS or Locale again)

    fun clearUserCountry(context: Context) {
        context.getSharedPreferences("geo_feature_prefs", Context.MODE_PRIVATE)
            .edit()
            .remove("user_country")
            .apply()
        Log.d(TAG, "User country override cleared")
    }

    // ==================== FEATURE QUERIES ====================


    fun getAllFeatures(callback: (List<GeoFeature>) -> Unit) {
        getController().fetchAllFeatures(object : FeaturesCallback {
            override fun onSuccess(features: List<GeoFeature>) {
                callback(features)
            }

            override fun onFailure(errorMessage: String) {
                Log.e(TAG, "Failed to fetch features: $errorMessage")
                callback(emptyList())
            }
        })
    }


    fun getFeatureByName(featureName: String, callback: (GeoFeature?) -> Unit) {
        getController().fetchFeatureByName(featureName, object : FeatureCallback {
            override fun onSuccess(feature: GeoFeature) {
                callback(feature)
            }

            override fun onFailure(errorMessage: String) {
                Log.e(TAG, "Failed to fetch feature '$featureName': $errorMessage")
                callback(null)
            }
        })
    }


     // Check if a feature is enabled for the device's current location

    fun isFeatureEnabled(
        context: Context,
        featureName: String,
        callback: (enabled: Boolean, value: String?) -> Unit
    ) {
        getCurrentCountry(context) { countryCode ->
            isFeatureEnabledForCountry(featureName, countryCode, callback)
        }
    }


     //Check if a feature is enabled for a specific country

    fun isFeatureEnabledForCountry(
        featureName: String,
        countryCode: String,
        callback: (enabled: Boolean, value: String?) -> Unit
    ) {
        getController().queryFeature(
            featureName,
            countryCode,
            object : FeatureQueryCallback {
                override fun onSuccess(response: FeatureQueryResponse) {
                    callback(response.enabled, response.value)
                }

                override fun onFailure(errorMessage: String) {
                    Log.e(TAG, "Failed to query feature '$featureName' for country '$countryCode': $errorMessage")
                    callback(false, null)
                }
            }
        )
    }

    /**
     * Get feature configuration for the device's current location
     */
    fun getFeatureConfig(
        context: Context,
        featureName: String,
        callback: (FeatureQueryResponse?) -> Unit
    ) {
        getCurrentCountry(context) { countryCode ->
            getFeatureConfigForCountry(featureName, countryCode, callback)
        }
    }


    fun getFeatureConfigForCountry(
        featureName: String,
        countryCode: String,
        callback: (FeatureQueryResponse?) -> Unit
    ) {
        getController().queryFeature(
            featureName,
            countryCode,
            object : FeatureQueryCallback {
                override fun onSuccess(response: FeatureQueryResponse) {
                    callback(response)
                }

                override fun onFailure(errorMessage: String) {
                    Log.e(TAG, "Failed to get feature config: $errorMessage")
                    callback(null)
                }
            }
        )
    }

    /**
     * Get list of countries that have rules for a specific feature
     *
     * @param featureName Name of the feature
     * @param callback Returns list of country codes
     */
    fun getFeatureCountries(featureName: String, callback: (List<String>) -> Unit) {
        getFeatureByName(featureName) { feature ->
            if (feature != null) {
                val countries = feature.geoRules.map { it.countryCode }
                Log.d(TAG, "Feature '$featureName' has rules for: $countries")
                callback(countries)
            } else {
                Log.w(TAG, "Feature '$featureName' not found")
                callback(emptyList())
            }
        }
    }

    /**
     * Check if a feature has a rule for a specific country
     *
     * @param featureName Name of the feature
     * @param countryCode ISO country code
     * @param callback Returns true if feature has a rule for this country
     */
    fun hasRuleForCountry(
        featureName: String,
        countryCode: String,
        callback: (Boolean) -> Unit
    ) {
        getFeatureByName(featureName) { feature ->
            if (feature != null) {
                val hasRule = feature.geoRules.any {
                    it.countryCode.equals(countryCode, ignoreCase = true)
                }
                callback(hasRule)
            } else {
                callback(false)
            }
        }
    }

    // ==================== ADMIN FUNCTIONS ====================

    /**
     * Admin function: Create a new feature
     * Use this for admin panels only
     */
    fun createFeature(feature: GeoFeature, callback: (GeoFeature?) -> Unit) {
        getController().createFeature(feature, object : FeatureCallback {
            override fun onSuccess(feature: GeoFeature) {
                callback(feature)
            }

            override fun onFailure(errorMessage: String) {
                Log.e(TAG, "Failed to create feature: $errorMessage")
                callback(null)
            }
        })
    }
    /**
     * Admin function: Update an existing feature
     */
    fun updateFeature(id: String, feature: GeoFeature, callback: (GeoFeature?) -> Unit) {
        getController().updateFeature(id, feature, object : FeatureCallback {
            override fun onSuccess(feature: GeoFeature) {
                callback(feature)
            }

            override fun onFailure(errorMessage: String) {
                Log.e(TAG, "Failed to update feature: $errorMessage")
                callback(null)
            }
        })
    }

    /**
     * Admin function: Delete a feature
     */
    fun deleteFeature(id: String, callback: (Boolean) -> Unit) {
        getController().deleteFeature(id, callback)
    }
}