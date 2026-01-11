package com.example.geofeaturesdk.models


data class CartItem(
    val product: Product,
    var quantity: Int = 1
) {
    fun getTotalPrice(): Double {
        return product.basePrice * quantity
    }
}


object ShoppingCart {
    private val items = mutableListOf<CartItem>()

    fun addItem(product: Product) {
        val existingItem = items.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            items.add(CartItem(product, 1))
        }
    }

    fun removeItem(productId: String) {
        items.removeIf { it.product.id == productId }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        items.find { it.product.id == productId }?.quantity = quantity
    }

    fun getItems(): List<CartItem> = items.toList()

    fun getTotalPrice(): Double = items.sumOf { it.getTotalPrice() }

    fun getItemCount(): Int = items.sumOf { it.quantity }

    fun clear() {
        items.clear()
    }
}