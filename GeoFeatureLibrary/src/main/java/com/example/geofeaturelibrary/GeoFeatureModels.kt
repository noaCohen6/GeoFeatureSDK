package com.example.geofeaturelibrary

import com.google.gson.annotations.SerializedName


data class GeoFeature(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("featureName")
    val featureName: String,

    @SerializedName("defaultStatus")
    val defaultStatus: Boolean,

    @SerializedName("geoRules")
    val geoRules: List<GeoRule> = emptyList(),

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
)


data class GeoRule(
    @SerializedName("countryCode")
    val countryCode: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("value")
    val value: String? = null
)


data class FeatureQueryRequest(
    @SerializedName("featureName")
    val featureName: String,

    @SerializedName("countryCode")
    val countryCode: String
)


data class FeatureQueryResponse(
    @SerializedName("enabled")
    val enabled: Boolean,

    @SerializedName("value")
    val value: String? = null
)