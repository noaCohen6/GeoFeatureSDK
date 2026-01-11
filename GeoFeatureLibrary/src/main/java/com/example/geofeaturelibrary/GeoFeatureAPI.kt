package com.example.geofeaturelibrary

import retrofit2.Call
import retrofit2.http.*

/**
 * Retrofit API interface for GeoFeature SDK
 */
interface GeoFeatureAPI {

    @GET("api/v1/features")
    fun getAllFeatures(): Call<List<GeoFeature>>


    @GET("api/v1/features/{id}")
    fun getFeatureById(
        @Path("id") id: String
    ): Call<GeoFeature>


    @GET("api/v1/features/by-name/{featureName}")
    fun getFeatureByName(
        @Path("featureName") featureName: String
    ): Call<GeoFeature>


    @POST("api/v1/features/query")
    fun queryFeature(
        @Body request: FeatureQueryRequest
    ): Call<FeatureQueryResponse>


    @POST("api/v1/features")
    fun createFeature(
        @Body feature: GeoFeature
    ): Call<GeoFeature>


    @PUT("api/v1/features/{id}")
    fun updateFeature(
        @Path("id") id: String,
        @Body feature: GeoFeature
    ): Call<GeoFeature>


    @DELETE("api/v1/features/{id}")
    fun deleteFeature(
        @Path("id") id: String
    ): Call<Void>
}