package com.example.geofeaturelibrary

import android.util.Log
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// Controller for managing API calls to GeoFeature SDK

class GeoFeatureController(private val baseUrl: String) {

    companion object {
        private const val TAG = "GeoFeatureController"
    }

    private val api: GeoFeatureAPI by lazy {
        createRetrofitInstance()
    }

    private fun createRetrofitInstance(): GeoFeatureAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setLenient()
                        .create()
                )
            )
            .build()

        return retrofit.create(GeoFeatureAPI::class.java)
    }


    fun fetchAllFeatures(callback: FeaturesCallback) {
        val call = api.getAllFeatures()

        call.enqueue(object : Callback<List<GeoFeature>> {
            override fun onResponse(
                call: Call<List<GeoFeature>>,
                response: Response<List<GeoFeature>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val errorMsg = "Error: ${response.code()} - ${response.message()}"
                    Log.e(TAG, errorMsg)
                    callback.onFailure(errorMsg)
                }
            }

            override fun onFailure(call: Call<List<GeoFeature>>, t: Throwable) {
                val errorMsg = "Network error: ${t.message}"
                Log.e(TAG, errorMsg, t)
                callback.onFailure(errorMsg)
            }
        })
    }


    fun fetchFeatureById(id: String, callback: FeatureCallback) {
        val call = api.getFeatureById(id)

        call.enqueue(object : Callback<GeoFeature> {
            override fun onResponse(
                call: Call<GeoFeature>,
                response: Response<GeoFeature>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val errorMsg = "Error: ${response.code()} - ${response.message()}"
                    Log.e(TAG, errorMsg)
                    callback.onFailure(errorMsg)
                }
            }

            override fun onFailure(call: Call<GeoFeature>, t: Throwable) {
                val errorMsg = "Network error: ${t.message}"
                Log.e(TAG, errorMsg, t)
                callback.onFailure(errorMsg)
            }
        })
    }


    fun fetchFeatureByName(featureName: String, callback: FeatureCallback) {
        val call = api.getFeatureByName(featureName)

        call.enqueue(object : Callback<GeoFeature> {
            override fun onResponse(
                call: Call<GeoFeature>,
                response: Response<GeoFeature>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val errorMsg = "Error: ${response.code()} - ${response.message()}"
                    Log.e(TAG, errorMsg)
                    callback.onFailure(errorMsg)
                }
            }

            override fun onFailure(call: Call<GeoFeature>, t: Throwable) {
                val errorMsg = "Network error: ${t.message}"
                Log.e(TAG, errorMsg, t)
                callback.onFailure(errorMsg)
            }
        })
    }


    fun queryFeature(
        featureName: String,
        countryCode: String,
        callback: FeatureQueryCallback
    ) {
        val request = FeatureQueryRequest(featureName, countryCode)
        val call = api.queryFeature(request)

        call.enqueue(object : Callback<FeatureQueryResponse> {
            override fun onResponse(
                call: Call<FeatureQueryResponse>,
                response: Response<FeatureQueryResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val errorMsg = "Error: ${response.code()} - ${response.message()}"
                    Log.e(TAG, errorMsg)
                    callback.onFailure(errorMsg)
                }
            }

            override fun onFailure(call: Call<FeatureQueryResponse>, t: Throwable) {
                val errorMsg = "Network error: ${t.message}"
                Log.e(TAG, errorMsg, t)
                callback.onFailure(errorMsg)
            }
        })
    }


     //Create a new feature (admin only)

    fun createFeature(feature: GeoFeature, callback: FeatureCallback) {
        val call = api.createFeature(feature)

        call.enqueue(object : Callback<GeoFeature> {
            override fun onResponse(
                call: Call<GeoFeature>,
                response: Response<GeoFeature>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val errorMsg = "Error: ${response.code()} - ${response.message()}"
                    Log.e(TAG, errorMsg)
                    callback.onFailure(errorMsg)
                }
            }

            override fun onFailure(call: Call<GeoFeature>, t: Throwable) {
                val errorMsg = "Network error: ${t.message}"
                Log.e(TAG, errorMsg, t)
                callback.onFailure(errorMsg)
            }
        })
    }
    fun updateFeature(id: String, feature: GeoFeature, callback: FeatureCallback) {
        val call = api.updateFeature(id, feature)

        call.enqueue(object : Callback<GeoFeature> {
            override fun onResponse(call: Call<GeoFeature>, response: Response<GeoFeature>) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val errorMsg = "Error: ${response.code()} - ${response.message()}"
                    callback.onFailure(errorMsg)
                }
            }

            override fun onFailure(call: Call<GeoFeature>, t: Throwable) {
                callback.onFailure("Network error: ${t.message}")
            }
        })
    }

    fun deleteFeature(id: String, callback: (Boolean) -> Unit) {
        val call = api.deleteFeature(id)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                callback(response.isSuccessful)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback(false)
            }
        })
    }
}