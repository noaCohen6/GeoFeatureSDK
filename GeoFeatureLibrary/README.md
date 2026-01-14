# 📚 GeoFeature Library

Android SDK for controlling app features based on user's country with automatic GPS detection.

---

## 📖 Integration Guide

### 1️⃣ Add Permissions (AndroidManifest.xml)

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

### 2️⃣ Request Permissions (MainActivity)

```kotlin
class MainActivity : AppCompatActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SDK
        GeoFeatureSDK.initialize("https://your-api.com/", this)

        // Request location permission
        requestLocationPermissionIfNeeded()

        // Load features
        loadGeoFeatures()
    }

    private fun requestLocationPermissionIfNeeded() {
        if (!GeoFeatureSDK.hasLocationPermission(this)) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadGeoFeatures()  // GPS enabled!
            }
            // If denied, SDK will use Locale automatically
        }
    }
}
```

### 3️⃣ Use the SDK

```kotlin
// Detect country (automatic!)
GeoFeatureSDK.getCurrentCountry(this) { country ->
    Log.d("App", "User in: $country")  // "IL", "US", "GB"...
}

// Get country name + flag
val name = GeoFeatureSDK.getCountryName("IL")
// → "Israel 🇮🇱"

// Check feature
GeoFeatureSDK.isFeatureEnabled(this, "dark_mode") { enabled, value ->
    if (enabled) {
        applyDarkTheme(value)  // value = "auto", "light", "dark"
    }
}

// Get payment methods
GeoFeatureSDK.isFeatureEnabled(this, "payment_methods") { enabled, value ->
    val methods = value?.split(",") ?: emptyList()
    // IL → ["credit_card", "paypal", "bit"]
    // US → ["credit_card", "paypal", "apple_pay"]
    showPaymentOptions(methods)
}

// Manual override (for testing)
GeoFeatureSDK.setUserCountry(this, "US")  // Set to USA
GeoFeatureSDK.clearUserCountry(this)      // Back to automatic
```

---

## 🏗️ Library Structure

```
geofeaturelibrary/
├── GeoFeatureSDK.kt              # ⭐ Main SDK - Public API
├── GeoLocationManager.kt         # 🌍 GPS & Geocoding (internal)
├── GeoFeatureController.kt       # 🌐 HTTP client (internal)
├── GeoFeatureAPI.kt              # 📡 Retrofit interface (internal)
├── GeoFeatureModels.kt           # 📦 Data classes
└── GeoFeatureCallbacks.kt        # 🔄 Callbacks
```

**Important:** Only use `GeoFeatureSDK` - all other classes are internal!

---

## 🌍 How It Works

```
1️⃣ GPS (if permission granted) 🎯 Most accurate
    ↓ if fails
2️⃣ Manual Override (if user set) 🧪 For testing
    ↓ if not set
3️⃣ Device Locale (phone language) 📱 Fallback
```

---

## 🌐 Backend Required

You need an API server: [GeoFeatureSDK-API](https://github.com/noaCohen6/GeoFeatureSDK-API)

---

## 📦 Dependencies

```gradle
dependencies {
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.google.code.gson:gson:2.10.1'
}
```

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](../LICENSE) file for details.

---

**For full documentation, see the [main README](../README.md).**
