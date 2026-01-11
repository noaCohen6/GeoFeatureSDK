package com.example.geofeaturesdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geofeaturelibrary.GeoFeatureSDK
import com.example.geofeaturesdk.adapters.FeatureStatusAdapter
import com.example.geofeaturesdk.models.FeatureStatus
import com.google.android.material.textview.MaterialTextView


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


    private fun loadFeatures() {
        GeoFeatureSDK.getCurrentCountry(this) { country ->
            currentCountry = country
            runOnUiThread {
                countryTextView.text = "Checking features for: $country"
                checkAllFeatures()
            }
        }
    }


    private fun checkAllFeatures() {
        val featuresToCheck = listOf(
            "dark_mode",
            "payment_methods",
            "currency_display",
            "black_friday_discount"
        )

        val featureStatuses = mutableListOf<FeatureStatus>()
        var checkedCount = 0

        featuresToCheck.forEach { featureName ->
            GeoFeatureSDK.isFeatureEnabled(this, featureName) { enabled, value ->
                runOnUiThread {
                    featureStatuses.add(
                        FeatureStatus(
                            name = featureName,
                            enabled = enabled,
                            value = value,
                            countryCode = currentCountry
                        )
                    )

                    checkedCount++
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