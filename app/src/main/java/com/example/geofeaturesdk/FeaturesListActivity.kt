package com.example.geofeaturesdk

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geofeaturelibrary.GeoFeatureSDK
import com.example.geofeaturesdk.adapters.FeatureStatusAdapter
import com.example.geofeaturesdk.models.FeatureStatus
import com.example.geofeaturesdk.utils.GeoHelper
import com.google.android.material.textview.MaterialTextView

/**
 * Debug screen displaying all features and their status for the current country.
 * Features are loaded dynamically from the server, no hard-coded list required.
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

    /**
     * Detect user's current country (Manual Override → GPS → Locale) and load features from server.
     */
    private fun loadFeatures() {
        GeoHelper.getCurrentCountry(this) { country ->
            currentCountry = country
            runOnUiThread {
                countryTextView.text = "📍 Checking features for: $country"
                loadAllFeaturesFromServer()
            }
        }
    }

    /**
     * Fetch all features dynamically from the server (no hard-coded list).
     */
    private fun loadAllFeaturesFromServer() {
        GeoFeatureSDK.getAllFeatures { features ->
            runOnUiThread {
                if (features.isEmpty()) {
                    Toast.makeText(this, "No features found", Toast.LENGTH_SHORT).show()
                    return@runOnUiThread
                }
                checkFeaturesForCountry(features)
            }
        }
    }

    /**
     * Check each feature's enabled status for the current country and update RecyclerView.
     */
    private fun checkFeaturesForCountry(features: List<com.example.geofeaturelibrary.GeoFeature>) {
        val featureStatuses = mutableListOf<FeatureStatus>()
        var checkedCount = 0

        features.forEach { feature ->
            GeoHelper.isFeatureEnabled(this, feature.featureName) { enabled, value ->
                runOnUiThread {
                    featureStatuses.add(
                        FeatureStatus(
                            name = feature.featureName,
                            enabled = enabled,
                            value = value,
                            countryCode = currentCountry
                        )
                    )

                    checkedCount++
                    if (checkedCount == features.size) {
                        featureAdapter.updateFeatures(featureStatuses.sortedBy { it.name })
                    }
                }
            }
        }
    }

    /**
     * Handle toolbar back button to close activity.
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}