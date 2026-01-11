package com.example.geofeaturesdk

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.geofeaturelibrary.GeoFeatureSDK
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView


class SettingsActivity : AppCompatActivity() {

    private lateinit var countrySpinner: Spinner
    private lateinit var currentCountryTextView: MaterialTextView
    private lateinit var useManualCountrySwitch: SwitchMaterial
    private lateinit var applyButton: MaterialButton
    private lateinit var clearButton: MaterialButton

    private val countries = listOf(
        "US" to "United States 🇺🇸",
        "IL" to "Israel 🇮🇱",
        "GB" to "United Kingdom 🇬🇧",
        "FR" to "France 🇫🇷",
        "DE" to "Germany 🇩🇪",
        "JP" to "Japan 🇯🇵"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "⚙️ Settings"

        initViews()
        loadCurrentCountry()
    }

    private fun initViews() {
        countrySpinner = findViewById(R.id.countrySpinner)
        currentCountryTextView = findViewById(R.id.currentCountryTextView)
        useManualCountrySwitch = findViewById(R.id.useManualCountrySwitch)
        applyButton = findViewById(R.id.applyButton)
        clearButton = findViewById(R.id.clearButton)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            countries.map { it.second }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countrySpinner.adapter = adapter

        useManualCountrySwitch.setOnCheckedChangeListener { _, isChecked ->
            countrySpinner.isEnabled = isChecked
            applyButton.isEnabled = isChecked

            if (!isChecked) {
                val manualCountry = GeoFeatureSDK.getUserCountry(this)
                if (manualCountry != null) {
                    GeoFeatureSDK.clearUserCountry(this)
                    Toast.makeText(this, "🔄 Switching back to auto-detection...", Toast.LENGTH_SHORT).show()
                    loadCurrentCountry()
                }
            }
        }

        applyButton.setOnClickListener {
            if (useManualCountrySwitch.isChecked) {
                val selectedIndex = countrySpinner.selectedItemPosition
                val selectedCountry = countries[selectedIndex].first
                GeoFeatureSDK.setUserCountry(this, selectedCountry)
                Toast.makeText(
                    this,
                    "✅ Country set to: ${countries[selectedIndex].second}",
                    Toast.LENGTH_SHORT
                ).show()
                loadCurrentCountry()
            }
        }

        // כפתור Clear - חזרה לאוטומטי
        clearButton.setOnClickListener {
            GeoFeatureSDK.clearUserCountry(this)
            useManualCountrySwitch.isChecked = false
            Toast.makeText(this, "✅ Cleared manual country - using GPS/Locale", Toast.LENGTH_SHORT).show()
            loadCurrentCountry()
        }
    }

    /**
     * טעינת המדינה הנוכחית
     */
    private fun loadCurrentCountry() {
        GeoFeatureSDK.getCurrentCountry(this) { country ->
            runOnUiThread {
                val manualCountry = GeoFeatureSDK.getUserCountry(this)
                val isManual = manualCountry != null

                val countryName = countries.find { it.first == country }?.second ?: country

                if (isManual) {
                    currentCountryTextView.text = "Current: $countryName (Manual Override)"
                } else {
                    currentCountryTextView.text = "Current: $countryName (Auto-detected)"
                }

                // עדכון Switch
                useManualCountrySwitch.isChecked = isManual

                // עדכון Spinner
                val countryIndex = countries.indexOfFirst { it.first == country }
                if (countryIndex >= 0) {
                    countrySpinner.setSelection(countryIndex)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        loadCurrentCountry()
    }
}