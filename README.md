# 🌍 GeoFeatureSDK

[![JitPack](https://jitpack.io/v/noaCohen6/GeoFeatureSDK.svg)](https://jitpack.io/#noaCohen6/GeoFeatureSDK)

**Android SDK + Demo App** for controlling app features based on user's country with automatic GPS detection.

---
**🌐 [View Full Documentation Site](https://noacohen6.github.io/GeoFeatureSDK/)**
---

## 📦 What's Inside?

This repository contains:

1. **📚 GeoFeature Library** - Android SDK for location-based features
2. **🛒 Demo App** - Shopping app showcasing the SDK capabilities

---

## 💡 What Does It Do?

Show different features to users in different countries - **automatically detected by GPS or device settings**.

**Real Examples from Demo App:**

### Israel 🇮🇱
- Prices in **₪ (Shekels)**
- Payment: Credit Card, PayPal, **Bit**
- **20%** Black Friday discount

### USA 🇺🇸
- Prices in **$ (Dollars)**
- Payment: Credit Card, PayPal, **Apple Pay**
- **50%** Black Friday discount

### UK 🇬🇧
- Prices in **£ (Pounds)**
- Payment: Credit Card, PayPal, Apple Pay
- **15%** Black Friday discount

---

## 🌟 Key Features

- 🌍 **Automatic Country Detection** - GPS → Manual Override → Device Locale
- 🎯 **Feature Toggles** - Enable/disable features per country
- 💾 **Custom Values** - Store country-specific configuration
- 🔧 **Manual Override** - Test different countries easily
- 📦 **Lightweight** - Only Retrofit + Gson
- 🔐 **Permission Handling** - Graceful fallback

---

## 🚀 Quick Start

### Option 1: Run the Demo App

```bash
# Clone the repo
git clone https://github.com/noaCohen6/GeoFeatureSDK.git
cd GeoFeatureSDK

# Open in Android Studio
# Run the app ▶️
```

**Note:** You'll need the API server running. See [Backend Setup](#-backend-setup) below.

---

### Option 2: Use the Library in Your App

#### Step 1: Add JitPack repository

Add this to your **settings.gradle.kts** (Project level):

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }  // Add this line
    }
}
```

---

#### Step 2: Add dependency

Add this to your **app/build.gradle.kts** (Module level):

```kotlin
dependencies {
    implementation("com.github.noaCohen6:GeoFeatureSDK:1.0.0")
}
```



---

#### Step 3: Sync Gradle & Use!

Click **"Sync Now"** in Android Studio, then see [Usage Example](#-usage-example) below.

**For detailed integration guide, see the [Library Documentation](./GeoFeatureLibrary/README.md).**

---

## 📂 Project Structure

```
GeoFeatureSDK/
├── GeoFeatureLibrary/            # 📚 The SDK Library
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/example/geofeaturelibrary/
│   │               ├── GeoFeatureSDK.kt              # Main SDK
│   │               ├── GeoLocationManager.kt         # GPS detection
│   │               ├── GeoFeatureController.kt       # API client
│   │               ├── GeoFeatureAPI.kt              # Retrofit interface
│   │               ├── GeoFeatureModels.kt           # Data models
│   │               └── GeoFeatureCallbacks.kt        # Callbacks
│   └── README.md                 # Library documentation
│
├── app/                          # 🛒 Demo Shopping App
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/example/geofeaturesdk/
│   │               ├── MainActivity.kt               # Store screen
│   │               ├── CheckoutActivity.kt           # Checkout
│   │               ├── SettingsActivity.kt           # Manual override
│   │               ├── FeaturesListActivity.kt       # Debug screen
│   │               ├── models/
│   │               ├── adapters/
│   │               └── utils/
│   └── README.md                 # Demo app documentation
│
├── build.gradle                  # Project build file
├── settings.gradle               # Project settings
└── README.md                     # This file
```

---

## 🌐 Backend Setup

**You need an API server to store features.**

I built a Spring Boot API for this SDK:

**Repository:** [GeoFeatureSDK-API](https://github.com/noaCohen6/GeoFeatureSDK-API)

### Quick Deploy (FREE):

```bash
# 1. Clone the API
git clone https://github.com/noaCohen6/GeoFeatureSDK-API.git

# 2. Deploy to Koyeb/Railway/Heroku
# Get your URL: https://your-api.koyeb.app/

# 3. Update MainActivity.kt
GeoFeatureSDK.initialize("https://your-api.koyeb.app/", this)
```

---

## 🎯 How It Works

```
Demo App
    ↓
GeoFeature Library (this SDK)
    ↓ 1. Detects country (GPS/Locale)
    ↓ 2. Queries features from API
    ↓ 3. Returns enabled/value
Your API Server
    ↓
MongoDB Database
```

**Country Detection Priority:**
```
1️⃣ GPS (if permission granted) 🎯 Most accurate
    ↓ if fails
2️⃣ Manual Override (if user set) 🧪 For testing
    ↓ if not set
3️⃣ Device Locale (phone language) 📱 Fallback
```

---


## 🔧 Usage Example

```kotlin
// Initialize SDK
GeoFeatureSDK.initialize("https://your-api.com/", this)

// Detect country
GeoFeatureSDK.getCurrentCountry(this) { country ->
    Log.d("App", "User in: $country")  // "IL", "US", "GB"...
}

// Get country name + flag
val name = GeoFeatureSDK.getCountryName("IL")
// → "Israel 🇮🇱"

// Check feature
GeoFeatureSDK.isFeatureEnabled(this, "dark_mode") { enabled, value ->
    if (enabled) {
        applyDarkTheme(value)
    }
}

// Manual override (testing)
GeoFeatureSDK.setUserCountry(this, "US")  // Set to USA
GeoFeatureSDK.clearUserCountry(this)      // Back to automatic
```

---

## 📚 Documentation

- **[Library Documentation](./GeoFeatureLibrary/README.md)** - How to integrate the SDK
- **[Demo App Documentation](./app/README.md)** - How to run the demo

---

## 🛠️ Tech Stack

**Library:**
- Kotlin
- Retrofit 2
- Gson
- Android Location Services
- Geocoder API

**Demo App:**
- Kotlin
- Material Design 3
- RecyclerView
- The GeoFeature Library

---

## 🔒 Permissions Required

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

---

## 🚨 Important Notes

### Library Does NOT Request Permissions!
The SDK only **checks** if permissions exist - **your app must request them**.

**Why?**
- Permissions should be requested by the app (MainActivity)
- Apps control when/how to ask users
- Better user experience

### Automatic Fallback
If GPS permission is denied, the SDK **automatically** uses device Locale:
```
Permission Denied → Locale → "IL" (if phone is in Hebrew)
```

### Network Required
SDK needs internet to fetch feature configurations from your API server.

---

## 🎓 Academic Project

Created as a learning project demonstrating:
- ✅ Android SDK development
- ✅ Location services (GPS + Geocoder)
- ✅ REST API integration
- ✅ Clean architecture
- ✅ Multi-module Android project

**Technologies:** Kotlin, Retrofit, Gson, Android Location Services

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/noaCohen6/GeoFeatureSDK.git
cd GeoFeatureSDK
```

### 2. Setup API Server

Deploy the backend API: [GeoFeatureSDK-API](https://github.com/noaCohen6/GeoFeatureSDK-API)

### 3. Configure API URL

Update `app/src/main/java/.../MainActivity.kt`:

```kotlin
GeoFeatureSDK.initialize("https://your-api-url.com/", this)
```

### 4. Open in Android Studio

```
File → Open → Select GeoFeatureSDK folder
```

### 5. Run

Click ▶️ Run and grant location permission when prompted.

---

## 🎯 Try Different Countries

**Method 1: Settings Screen (Recommended)**
1. Open app → Menu → Settings
2. Toggle "Manual Country Override"
3. Select country (IL/US/GB)
4. See instant changes

**Method 2: Real GPS**
- Grant location permission
- App detects your real location

**Method 3: Phone Language**
- Change phone language
- App uses Locale as fallback

---

## 🌟 Use Cases

- 🛒 **E-commerce** - Currency, payment methods, shipping
- 🍔 **Food Delivery** - Menu items, prices per region
- 📰 **News Apps** - Content restrictions, languages
- 🎮 **Games** - Regional events, pricing
- 💰 **FinTech** - Payment options, regulations
- 📱 **Any App** - Localized features

---

## 🤝 Contributing

This is an academic project, but suggestions are welcome!

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License**.

```
MIT License

Copyright (c) 2026 Noa Cohen

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

See the [LICENSE](LICENSE) file for full details.

---

## 🔗 Related Repositories

- **[GeoFeatureSDK-API](https://github.com/noaCohen6/GeoFeatureSDK-API)** - Spring Boot backend API

---



**⭐ If you find this project useful, please star it!**
