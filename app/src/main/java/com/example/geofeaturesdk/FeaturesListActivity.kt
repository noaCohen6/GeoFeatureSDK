package com.example.geofeaturesdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geofeaturesdk.adapters.FeatureStatusAdapter
import com.example.geofeaturesdk.models.FeatureStatus
import com.example.geofeaturesdk.utils.GeoHelper
import com.google.android.material.textview.MaterialTextView

/**
 * Debug screen showing all features and their status for the current country.
 * Uses GeoHelper to respect manual country override from Settings.
 */
class FeaturesListActivity : AppCompatActivity() {

    private lateinit var countryTextView: MaterialTextView
    private lateinit var featuresRecyclerView: RecyclerView
    private lateinit var featureAdapter: FeatureStatusAdapter

    private var currentCountry = "US"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_features_list)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "🎯 Features Status"

        initViews()
        loadFeatures()
    }


    private fun initViews() {
        countryTextView = findViewById(R.id.countryTextView)
        featuresRecyclerView = findViewById(R.id.featuresRecyclerView)

        featureAdapter = FeatureStatusAdapter(emptyList())
        featuresRecyclerView.layoutManager = LinearLayoutManager(this)
        featuresRecyclerView.adapter = featureAdapter
    }


     //Detect current country and load features.
     //GeoHelper checks manual override first, then falls back to SDK (GPS/Locale).
    private fun loadFeatures() {
        // Get current country (respects manual override if set in Settings)
        GeoHelper.getCurrentCountry(this) { country ->
            currentCountry = country
            runOnUiThread {
                countryTextView.text = "Checking features for: $country"
                checkAllFeatures()
            }
        }
    }

    /**
     * Check all features for the current country.
     * Queries each feature from the server and displays the results.
     */
    private fun checkAllFeatures() {
        val featuresToCheck = listOf(
            "payment_methods",
            "currency_display",
            "black_friday_discount"
        )

        val featureStatuses = mutableListOf<FeatureStatus>()
        var checkedCount = 0

        featuresToCheck.forEach { featureName ->
            // Check if feature is enabled for current country
            // Flow: GeoHelper → SDK → Server API → callback with result
            GeoHelper.isFeatureEnabled(this, featureName) { enabled, value ->
                runOnUiThread {
                    // Add feature status to list
                    featureStatuses.add(
                        FeatureStatus(
                            name = featureName,
                            enabled = enabled,
                            value = value,
                            countryCode = currentCountry
                        )
                    )

                    checkedCount++
                    // When all features checked, update the UI
                    if (checkedCount == featuresToCheck.size) {
                        featureAdapter.updateFeatures(featureStatuses.sortedBy { it.name })
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}