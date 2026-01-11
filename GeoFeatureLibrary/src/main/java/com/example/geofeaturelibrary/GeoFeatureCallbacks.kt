package com.example.geofeaturelibrary


interface FeatureCallback {
    fun onSuccess(feature: GeoFeature)
    fun onFailure(errorMessage: String)
}


interface FeaturesCallback {
    fun onSuccess(features: List<GeoFeature>)
    fun onFailure(errorMessage: String)
}


interface FeatureQueryCallback {
    fun onSuccess(response: FeatureQueryResponse)
    fun onFailure(errorMessage: String)
}

