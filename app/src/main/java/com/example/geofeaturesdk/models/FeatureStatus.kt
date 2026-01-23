package com.example.geofeaturesdk.models


data class FeatureStatus(
    val name: String,
    val enabled: Boolean,
    val value: String?,
    val countryCode: String
) {

    fun getIcon(): String {
        return when (name.lowercase()) {
            "dark_mode" -> "🌙"
            "payment_methods" -> "💳"
            "currency_display" -> "💰"
            "black_friday_discount" -> "🎉"
            "premium_shipping" -> "✈️"
            "special_offers" -> "🎁"
            "customer_support_chat" -> "💬"
            "loyalty_program" -> "⭐"
            else -> "🎯"
        }
    }


    fun getDescription(): String {
        return when (name.lowercase()) {
            "dark_mode" -> "Dark theme for the app"
            "payment_methods" -> "Available payment options"
            "currency_display" -> "Currency format by country"
            "black_friday_discount" -> "Black Friday sale discount"
            "premium_shipping" -> "Fast delivery options"
            "special_offers" -> "Region-specific promotions"
            "customer_support_chat" -> "Live chat support"
            "loyalty_program" -> "Rewards and points"
            else -> "Feature configuration"
        }
    }
}