package com.example.geofeaturesdk.models


data class Product(
    val id: String,
    val name: String,
    val description: String,
    val basePrice: Double,
    val imageEmoji: String,
    val category: String
) {
    companion object {

        fun getSampleProducts(): List<Product> {
            return listOf(
                Product(
                    id = "1",
                    name = "Wireless Headphones",
                    description = "Premium noise-canceling headphones",
                    basePrice = 299.99,
                    imageEmoji = "🎧",
                    category = "Electronics"
                ),
                Product(
                    id = "2",
                    name = "Smart Watch",
                    description = "Fitness tracker with heart rate monitor",
                    basePrice = 199.99,
                    imageEmoji = "⌚",
                    category = "Electronics"
                ),
                Product(
                    id = "3",
                    name = "Coffee Maker",
                    description = "Automatic espresso machine",
                    basePrice = 449.99,
                    imageEmoji = "☕",
                    category = "Home"
                ),
                Product(
                    id = "4",
                    name = "Running Shoes",
                    description = "Lightweight athletic shoes",
                    basePrice = 129.99,
                    imageEmoji = "👟",
                    category = "Fashion"
                ),
                Product(
                    id = "5",
                    name = "Camera",
                    description = "4K digital camera with WiFi",
                    basePrice = 799.99,
                    imageEmoji = "📷",
                    category = "Electronics"
                ),
                Product(
                    id = "6",
                    name = "Backpack",
                    description = "Water-resistant travel backpack",
                    basePrice = 89.99,
                    imageEmoji = "🎒",
                    category = "Fashion"
                )
            )
        }
    }
}