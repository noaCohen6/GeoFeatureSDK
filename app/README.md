# 🛒 GeoFeature Demo App

Shopping app demonstrating the **GeoFeature SDK** with automatic location-based features.

---

## 💡 What It Shows

### Israel 🇮🇱
- Prices in **₪ (Shekels)**
- Payment: Credit Card, PayPal, **Bit**
- **20%** Black Friday discount

### USA 🇺🇸
- Prices in **$ (Dollars)**
- Payment: Credit Card, PayPal, **Apple Pay**
- **30%** Black Friday discount

### UK 🇬🇧
- Prices in **£ (Pounds)**
- Payment: Credit Card, PayPal, Apple Pay
- **15%** Black Friday discount

---

## 🚀 How to Run

### 1️⃣ Setup API Server

Deploy the backend: [GeoFeatureSDK-API](https://github.com/noaCohen6/GeoFeatureSDK-API)

### 2️⃣ Configure URL

Update `MainActivity.kt`:

```kotlin
GeoFeatureSDK.initialize("https://your-api.koyeb.app/", this)
```

### 3️⃣ Run

Open in Android Studio → Click ▶️ Run

### 4️⃣ Grant Permission

When prompted, allow location access to enable GPS detection.

---

## 📱 App Screens

- **🏪 Store** - Products with auto-detected currency
- **🛒 Checkout** - Country-specific payment methods
- **⚙️ Settings** - Manual country override (for testing/demo)
- **📋 Features** - Debug screen showing all active features

---

## 🎯 Try Different Countries

### Automatic Detection (Default):
The app automatically detects your country via GPS → shows local currency, payment methods, and discounts.

### Manual Override (For Testing):
1. Menu → Settings
2. Toggle "Manual Country Override" ON
3. Select country: IL / US / UK / FR / DE / JP
4. Click "Apply"
5. See instant changes in currency, payments, and discounts
6. Toggle OFF or click "Clear" to return to GPS detection

**Note:** Manual override is stored locally in the app (SharedPreferences) and doesn't affect your phone's language or location settings.

---

## 🔧 Code Examples

### Using GeoHelper (App-Level Wrapper)

The demo app uses `GeoHelper` to respect manual country override for testing purposes:

```kotlin
// Detect country (respects manual override)
GeoHelper.getCurrentCountry(this) { country ->
    val name = GeoFeatureSDK.getCountryName(country)
    locationTextView.text = "📍 $name"
}

// Check if feature is enabled
GeoHelper.isFeatureEnabled(this, "payment_methods") { enabled, value ->
    val methods = value?.split(",") ?: emptyList()
    showPaymentMethods(methods)
}

// Apply discount
GeoHelper.isFeatureEnabled(this, "black_friday_discount") { enabled, value ->
    val discount = value?.toIntOrNull() ?: 0
    applyDiscount(discount)
}
```

### Using SDK Directly (Production)

For production apps, use the SDK directly (GPS has priority):

```kotlin
// Direct SDK usage (GPS → Locale fallback)
GeoFeatureSDK.getCurrentCountry(this) { country ->
    // Country detected via GPS or device locale
}

// Check feature
GeoFeatureSDK.isFeatureEnabled(this, "payment_methods") { enabled, value ->
    // Feature check based on detected country
}

// Manual override (if needed)
GeoFeatureSDK.setUserCountry(this, "US")  // Set manually
GeoFeatureSDK.clearUserCountry(this)      // Clear override
```

---

## 🏗️ Project Structure

```
app/
├── MainActivity.kt              # Store screen (uses GeoHelper)
├── CheckoutActivity.kt          # Checkout (uses GeoHelper)
├── SettingsActivity.kt          # Manual override settings
├── FeaturesListActivity.kt      # Debug screen (uses GeoHelper)
├── models/
│   ├── Product.kt
│   ├── CartItem.kt
│   └── FeatureStatus.kt
├── adapters/
│   ├── ProductAdapter.kt
│   ├── CartAdapter.kt
│   └── FeatureStatusAdapter.kt
└── utils/
    ├── GeoHelper.kt             # App wrapper for manual override
    └── CurrencyFormatter.kt
```

---

## 🎯 GeoHelper vs SDK Direct Usage

| Feature | GeoHelper (Demo App) | SDK Direct (Production) |
|---------|---------------------|------------------------|
| **Priority** | Manual Override → GPS → Locale | GPS → Manual Override → Locale |
| **Use Case** | Testing/Demo apps | Production apps |
| **When to use** | When you want manual override for demos | When GPS should be primary |
| **Location** | App-level wrapper | SDK itself |

### Why GeoHelper?

The **GeoHelper** is an app-level wrapper that allows manual country override to take priority over GPS. This is useful for:
- 🎯 **Demo/Testing**: Show how features work in different countries without traveling
- 🧪 **Development**: Test country-specific logic locally
- 📊 **Presentations**: Demonstrate geo-based features to clients

In production apps, you typically use the SDK directly where GPS detection is the primary method.

---

## 🌍 How Location Detection Works

### Priority Order in Demo App (with GeoHelper):
1. **Manual Override** (if set in Settings) → User's explicit choice
2. **GPS** (if permission granted) → Real-time location
3. **Device Locale** (fallback) → Phone language/region settings

### Priority Order in Production (SDK Direct):
1. **GPS** (if permission granted) → Real-time location
2. **Manual Override** (if set) → User preference
3. **Device Locale** (fallback) → Phone language/region settings

---

## 🔐 Permissions

The app requests:
- `ACCESS_FINE_LOCATION` - For precise GPS detection
- `ACCESS_COARSE_LOCATION` - For network-based location

Without these permissions, the app falls back to device locale (phone language settings).

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](../LICENSE) file for details.

---

**For full SDK documentation, see the [main README](../README.md).**
