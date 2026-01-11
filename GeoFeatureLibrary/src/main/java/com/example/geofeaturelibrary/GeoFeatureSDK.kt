package com.example.geofeaturelibrary

import android.content.Context
import android.util.Log
import java.util.Locale


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


    private fun getController(): GeoFeatureController {
        if (!::controller.isInitialized) {
            Log.w(TAG, "Controller not initialized, using default URL: $DEFAULT_BASE_URL")
            controller = GeoFeatureController(DEFAULT_BASE_URL)
        }
        return controller
    }


    private fun getDeviceCountryCode(context: Context): String {
        return try {
            val locale = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                // Android 7.0+ (API 24+)
                context.resources.configuration.locales[0]
            } else {
                // Android 6.0 ומטה
                @Suppress("DEPRECATION")
                context.resources.configuration.locale
            }
            locale.country.uppercase()  // "IL", "US", "GB", etc.
        } catch (e: Exception) {
            Log.e(TAG, "Error getting country code from locale", e)
            Locale.getDefault().country.uppercase()
        }
    }


    fun getCurrentCountry(context: Context, callback: (String) -> Unit) {
        //   GPS
        if (isLocationInitialized && locationManager.hasLocationPermission()) {
            locationManager.getCountryFromLocation { gpsCountry ->
                if (gpsCountry != null) {
                    Log.d(TAG, "Country from GPS: $gpsCountry")
                    callback(gpsCountry)
                } else {
                    //  Manual Override
                    val userCountry = getUserCountry(context)
                    if (userCountry != null) {
                        Log.d(TAG, "GPS failed, using manual override: $userCountry")
                        callback(userCountry)
                    } else {
                        val localeCountry = getDeviceCountryCode(context)
                        Log.d(TAG, "GPS failed, no manual override, using Locale: $localeCountry")
                        callback(localeCountry)
                    }
                }
            }
        } else {
            val userCountry = getUserCountry(context)
            if (userCountry != null) {
                Log.d(TAG, "No GPS permission, using manual override: $userCountry")
                callback(userCountry)
            } else {
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


    fun isInCountry(context: Context, countryCode: String, callback: (Boolean) -> Unit) {
        getCurrentCountry(context) { currentCountry ->
            val isInCountry = currentCountry.equals(countryCode, ignoreCase = true)
            Log.d(TAG, "Is in $countryCode? $isInCountry (current: $currentCountry)")
            callback(isInCountry)
        }
    }


    fun hasLocationPermission(context: Context): Boolean {
        return if (isLocationInitialized) {
            locationManager.hasLocationPermission()
        } else {
            false
        }
    }


    fun setUserCountry(context: Context, countryCode: String) {
        context.getSharedPreferences("geo_feature_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("user_country", countryCode.uppercase())
            .apply()
        Log.d(TAG, "User country manually set to: $countryCode")
    }


    fun getUserCountry(context: Context): String? {
        return context.getSharedPreferences("geo_feature_prefs", Context.MODE_PRIVATE)
            .getString("user_country", null)
    }


    fun clearUserCountry(context: Context) {
        context.getSharedPreferences("geo_feature_prefs", Context.MODE_PRIVATE)
            .edit()
            .remove("user_country")
            .apply()
        Log.d(TAG, "User country override cleared")
    }



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


    fun isFeatureEnabled(
        context: Context,
        featureName: String,
        callback: (enabled: Boolean, value: String?) -> Unit
    ) {
        getCurrentCountry(context) { countryCode ->
            isFeatureEnabledForCountry(featureName, countryCode, callback)
        }
    }


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

    //Admin only
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

    //Admin only
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

   //Admin only
    fun deleteFeature(id: String, callback: (Boolean) -> Unit) {
        getController().deleteFeature(id, callback)
    }



    fun getCountryName(countryCode: String): String {
        return when (countryCode.uppercase()) {
            "IL" -> "Israel 🇮🇱"
            "US" -> "United States 🇺🇸"
            "GB", "UK" -> "United Kingdom 🇬🇧"
            "FR" -> "France 🇫🇷"
            "DE" -> "Germany 🇩🇪"
            "ES" -> "Spain 🇪🇸"
            "IT" -> "Italy 🇮🇹"
            "JP" -> "Japan 🇯🇵"
            "CN" -> "China 🇨🇳"
            "IN" -> "India 🇮🇳"
            "BR" -> "Brazil 🇧🇷"
            "CA" -> "Canada 🇨🇦"
            "AU" -> "Australia 🇦🇺"
            "MX" -> "Mexico 🇲🇽"
            "RU" -> "Russia 🇷🇺"
            "KR" -> "South Korea 🇰🇷"
            "AR" -> "Argentina 🇦🇷"
            "NL" -> "Netherlands 🇳🇱"
            "SE" -> "Sweden 🇸🇪"
            "NO" -> "Norway 🇳🇴"
            "DK" -> "Denmark 🇩🇰"
            "FI" -> "Finland 🇫🇮"
            "PL" -> "Poland 🇵🇱"
            "TR" -> "Turkey 🇹🇷"
            "SA" -> "Saudi Arabia 🇸🇦"
            "AE" -> "UAE 🇦🇪"
            "EG" -> "Egypt 🇪🇬"
            "ZA" -> "South Africa 🇿🇦"
            else -> countryCode.uppercase()
        }
    }



}