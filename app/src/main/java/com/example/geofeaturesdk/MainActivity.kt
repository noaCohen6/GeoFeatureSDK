package com.example.geofeaturesdk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geofeaturelibrary.GeoFeatureSDK
import com.example.geofeaturesdk.adapters.ProductAdapter
import com.example.geofeaturesdk.models.Product
import com.example.geofeaturesdk.models.ShoppingCart
import com.example.geofeaturesdk.utils.CurrencyFormatter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {

    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var locationTextView: MaterialTextView
    private lateinit var currencyTextView: MaterialTextView
    private lateinit var statusTextView: MaterialTextView
    private lateinit var checkoutButton: MaterialButton
    private lateinit var productAdapter: ProductAdapter

    private var currentCountry = "US"

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "🛒 GeoFeature Demo"

        initViews()

        // KOYEB!
        GeoFeatureSDK.initialize("https://thundering-elfie-geofeature-a5030253.koyeb.app/", this)

        if (!hasLocationPermission()) {
            requestLocationPermission()
        } else {
            loadGeoFeatures()
        }

        loadProducts()
    }

    private fun initViews() {
        locationTextView = findViewById(R.id.locationTextView)
        currencyTextView = findViewById(R.id.currencyTextView)
        statusTextView = findViewById(R.id.statusTextView)
        checkoutButton = findViewById(R.id.checkoutButton)
        productsRecyclerView = findViewById(R.id.productsRecyclerView)

        productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        productAdapter = ProductAdapter(
            products = emptyList(),
            currentCountry = currentCountry,
            onAddToCart = { product ->
                ShoppingCart.addItem(product)
                updateCartBadge()
                Toast.makeText(this, "${product.name} added! 🛒", Toast.LENGTH_SHORT).show()
            }
        )
        productsRecyclerView.adapter = productAdapter

        checkoutButton.setOnClickListener {
            if (ShoppingCart.getItemCount() == 0) {
                Toast.makeText(this, "Cart is empty! 🛒", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, CheckoutActivity::class.java))
            }
        }
    }

    private fun loadProducts() {
        val products = Product.getSampleProducts()
        productAdapter.updateProducts(products, currentCountry)
    }

    /**
     * טעינת GeoFeatures - ישירות מה-API!
     */
    private fun loadGeoFeatures() {
        statusTextView.text = "Loading..."

        GeoFeatureSDK.getCurrentCountry(this) { country ->
            currentCountry = country
            runOnUiThread {
                updateLocationUI(country)
                checkFeatures()
            }
        }
    }

    /**
     * בדיקת פיצ'רים - ישירות מה-API!
     */
    private fun checkFeatures() {
        var messages = mutableListOf<String>()

        // 1. Dark Mode
        GeoFeatureSDK.isFeatureEnabled(this, "dark_mode") { enabled, _ ->
            if (enabled) {
                messages.add("🌙 Dark mode available")
            }
        }

        // 2. Payment Methods - ישירות מה-VALUE!
        GeoFeatureSDK.isFeatureEnabled(this, "payment_methods") { enabled, value ->
            if (enabled && value != null) {
                val count = value.split(",").size
                messages.add("💳 $count payment methods")
            }
        }

        // 3. Black Friday - ישירות מה-VALUE!
        GeoFeatureSDK.isFeatureEnabled(this, "black_friday_discount") { enabled, value ->
            runOnUiThread {
                if (enabled && value != null) {
                    val discount = value.toIntOrNull() ?: 0
                    messages.add("🎉 $discount% Black Friday discount!")
                    Toast.makeText(this, "🎉 Black Friday Sale: $discount% OFF!", Toast.LENGTH_LONG).show()
                }

                // הצג הכל
                statusTextView.text = if (messages.isEmpty()) {
                    "✅ Connected to API"
                } else {
                    messages.joinToString(" • ")
                }
            }
        }
    }

    private fun updateLocationUI(country: String) {
        val countryName = getCountryName(country)
        val currencyInfo = CurrencyFormatter.getCurrencyInfo(country)

        locationTextView.text = "📍 $countryName"
        currencyTextView.text = "💰 ${currencyInfo.name} (${currencyInfo.symbol})"

        productAdapter.updateCountry(country)
    }

    private fun getCountryName(code: String): String {
        return when (code.uppercase()) {
            "IL" -> "Israel 🇮🇱"
            "US" -> "United States 🇺🇸"
            "GB" -> "United Kingdom 🇬🇧"
            "FR" -> "France 🇫🇷"
            "DE" -> "Germany 🇩🇪"
            else -> code
        }
    }

    private fun updateCartBadge() {
        invalidateOptionsMenu()
    }

    // ================ Menu ================

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val cartItem = menu.findItem(R.id.action_cart)
        val itemCount = ShoppingCart.getItemCount()
        if (itemCount > 0) {
            cartItem.title = "Cart ($itemCount)"
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                if (ShoppingCart.getItemCount() == 0) {
                    Toast.makeText(this, "Cart is empty! 🛒", Toast.LENGTH_SHORT).show()
                } else {
                    startActivity(Intent(this, CheckoutActivity::class.java))
                }
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_features -> {
                startActivity(Intent(this, FeaturesListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ================ Permissions ================

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "✅ Location permission granted")
                loadGeoFeatures()
            } else {
                Log.w(TAG, "❌ Location permission denied - using locale")
                loadGeoFeatures()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
        loadGeoFeatures()

    }
}