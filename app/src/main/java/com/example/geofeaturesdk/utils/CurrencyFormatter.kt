package com.example.geofeaturesdk.utils


object CurrencyFormatter {

    data class CurrencyInfo(
        val symbol: String,
        val name: String,
        val code: String
    )


    fun getCurrencyInfo(countryCode: String): CurrencyInfo {
        return when (countryCode.uppercase()) {
            "IL" -> CurrencyInfo("₪", "שקל", "ILS")
            "US" -> CurrencyInfo("$", "Dollar", "USD")
            "GB" -> CurrencyInfo("£", "Pound", "GBP")
            "EU", "FR", "DE", "IT", "ES" -> CurrencyInfo("€", "Euro", "EUR")
            "JP" -> CurrencyInfo("¥", "Yen", "JPY")
            "CN" -> CurrencyInfo("¥", "Yuan", "CNY")
            else -> CurrencyInfo("$", "Dollar", "USD")
        }
    }


    fun convertPrice(basePrice: Double, countryCode: String): Double {
        return when (countryCode.uppercase()) {
            "IL" -> basePrice * 3.7  // דולר לשקל
            "GB" -> basePrice * 0.82 // דולר לליש"ט
            "EU", "FR", "DE", "IT", "ES" -> basePrice * 0.95 // דולר ליורו
            "JP" -> basePrice * 150.0 // דולר ליין
            else -> basePrice // מחיר בסיס בדולר
        }
    }


    fun formatPrice(price: Double, countryCode: String): String {
        val currency = getCurrencyInfo(countryCode)
        val convertedPrice = convertPrice(price, countryCode)

        return when (countryCode.uppercase()) {
            "IL" -> String.format("₪%.2f", convertedPrice)
            "US" -> String.format("$%.2f", convertedPrice)
            "GB" -> String.format("£%.2f", convertedPrice)
            "EU", "FR", "DE", "IT", "ES" -> String.format("€%.2f", convertedPrice)
            else -> String.format("$%.2f", convertedPrice)
        }
    }
}