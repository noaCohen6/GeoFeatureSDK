# Changelog

All notable changes to the GeoFeature SDK project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---


## [1.0.0] - 2026-01-14

### 🎉 Initial Release - Published on JitPack

First stable release of GeoFeature SDK, now available via JitPack!

#### Added - Android Library
- 🌍 Automatic location detection (GPS + Geocoder + Device Locale)
- 🎯 Feature toggle system with geographic rules
- 💰 Support for custom values per country
- 🔄 Real-time feature queries via REST API
- 🔐 Manual country override functionality
- 📱 Kotlin-first API design
- 🚀 Retrofit integration for networking
- 📦 **Published to JitPack** - `com.github.noaCohen6:GeoFeatureSDK:1.0.0`

#### Added - Demo Application
- 🛒 E-commerce sample app
- 💳 Country-specific payment methods
- 🎉 Regional discount system
- 💱 Multi-currency support (ILS, USD, GBP, EUR)
- ⚙️ Settings page for manual country override
- 📋 Feature status viewer
- 🌍 Real-time country detection

#### Technical Details
- **Language:** Kotlin
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 36
- **Build System:** Gradle 8.14
- **Java Version:** 11

#### Dependencies
- Retrofit 2.9.0
- Retrofit Converter Gson 2.9.0
- Gson 2.10.1
- OkHttp Logging Interceptor 4.12.0
- AndroidX Core KTX 1.12.0

#### Documentation
- 📖 Comprehensive README with usage examples
- 📚 Library integration guide
- 🎓 Demo app documentation
- 🔧 Setup instructions for backend API

---

## Version Guidelines

Following [Semantic Versioning](https://semver.org/):

- **MAJOR** (X.0.0): Breaking API changes
- **MINOR** (0.X.0): New features, backwards-compatible
- **PATCH** (0.0.X): Bug fixes, backwards-compatible

### Version Strategy

- **0.x.x**: Initial development phase - breaking changes allowed
- **1.0.0**: First stable release - used in production ✅
- **1.x.x**: Stable releases - backwards compatibility guaranteed

---

## Categories Used

- **Added**: New features
- **Changed**: Changes in existing functionality
- **Deprecated**: Soon-to-be removed features
- **Removed**: Removed features
- **Fixed**: Bug fixes
- **Security**: Security vulnerabilities fixed

---

## Installation

```gradle
// Add JitPack repository
maven { url 'https://jitpack.io' }

// Add dependency
implementation 'com.github.noaCohen6:GeoFeatureSDK:1.0.0'
```

See [README](README.md) for full installation instructions.

---

[Unreleased]: https://github.com/noaCohen6/GeoFeatureSDK/compare/1.0.0...HEAD
[1.0.0]: https://github.com/noaCohen6/GeoFeatureSDK/releases/tag/1.0.0