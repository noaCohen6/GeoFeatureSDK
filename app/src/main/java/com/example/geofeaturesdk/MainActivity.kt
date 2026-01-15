package com.example.geofeaturesdk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geofeaturelibrary.GeoFeatureSDK
import com.example.geofeaturesdk.adapters.ProductAdapter
import com.example.geofeaturesdk.models.Product
import com.example.geofeaturesdk.models.ShoppingCart
import com.example.geofeaturesdk.utils.CurrencyFormatter
import com.example.geofeaturesdk.utils.GeoHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView


class MainActivity : AppCompatActivity() {

    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var locationTextView: MaterialTextView
    private lateinit var currencyTextView: MaterialTextView
    private lateinit var statusTextView: MaterialTextView
    private lateinit var checkoutButton: MaterialButton
    private lateinit var productAdapter: ProductAdapter

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
    private var currentCountry = "US"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "🛒 GeoFeature Demo"

        initViews()

        //KOYEB
        GeoFeatureSDK.initialize("https://thundering-elfie-geofeature-a5030253.koyeb.app/", this)
        requestLocationPermissionIfNeeded()

        loadProducts()
        loadGeoFeatures()
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


    private fun loadGeoFeatures() {
        statusTextView.text = "Loading..."

        // Using GeoHelper instead of SDK directly!
        // This respects manual override even when GPS is available
        //For Tasting
        GeoHelper.getCurrentCountry(this) { country ->
            currentCountry = country
            runOnUiThread {
                updateLocationUI(country)
                checkFeatures()
            }
        }
    }


    private fun checkFeatures() {
        val messages = mutableListOf<String>()



        // 1. Payment Methods
        GeoHelper.isFeatureEnabled(this, "payment_methods") { enabled, value ->
            if (enabled && value != null) {
                val count = value.split(",").size
                messages.add("💳 $count payment methods")
            }
        }

        // 2. Black Friday
        GeoHelper.isFeatureEnabled(this, "black_friday_discount") { enabled, value ->
            runOnUiThread {
                if (enabled && value != null) {
                    val discount = value.toIntOrNull() ?: 0
                    messages.add("🎉 $discount% Black Friday discount!")
                    Toast.makeText(this, "🎉 Black Friday Sale: $discount% OFF!", Toast.LENGTH_LONG).show()
                }

                statusTextView.text = if (messages.isEmpty()) {
                    "✅ Connected to API"
                } else {
                    messages.joinToString(" • ")
                }
            }
        }
    }


    private fun updateLocationUI(country: String) {
        val countryName = GeoFeatureSDK.getCountryName(country)
        val currencyInfo = CurrencyFormatter.getCurrencyInfo(country)

        locationTextView.text = "📍 $countryName"
        currencyTextView.text = "💰 ${currencyInfo.name} (${currencyInfo.symbol})"

        productAdapter.updateCountry(country)
    }

    private fun updateCartBadge() {
        invalidateOptionsMenu()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val cartItem = menu.findItem(R.id.action_cart)
        val itemCount = ShoppingCart.getItemCount()
        if (itemCount > 0) {
            cartItem.title = "Cart ($itemCount)"
        }
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)

            if (menuItem.itemId != R.id.action_cart) {
                val spannableTitle = SpannableString(menuItem.title)
                spannableTitle.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    0,
                    spannableTitle.length,
                    0
                )
                menuItem.title = spannableTitle
            }
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
                Toast.makeText(this, "✅ GPS enabled!", Toast.LENGTH_SHORT).show()
                loadGeoFeatures()
            } else {
                Toast.makeText(this, "⚠️ Using Locale", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
        loadGeoFeatures()
    }
}