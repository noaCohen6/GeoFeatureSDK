package com.example.geofeaturesdk.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.geofeaturesdk.R
import com.example.geofeaturesdk.models.CartItem
import com.example.geofeaturesdk.utils.CurrencyFormatter


class CartAdapter(
    private var items: List<CartItem>,
    private var currentCountry: String,
    private val onQuantityChanged: (String, Int) -> Unit,
    private val onRemove: (String) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emojiTextView: TextView = itemView.findViewById(R.id.cartItemEmoji)
        val nameTextView: TextView = itemView.findViewById(R.id.cartItemName)
        val priceTextView: TextView = itemView.findViewById(R.id.cartItemPrice)
        val quantityTextView: TextView = itemView.findViewById(R.id.cartItemQuantity)
        val decreaseButton: ImageButton = itemView.findViewById(R.id.decreaseButton)
        val increaseButton: ImageButton = itemView.findViewById(R.id.increaseButton)
        val removeButton: ImageButton = itemView.findViewById(R.id.removeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = items[position]
        val product = cartItem.product

        holder.emojiTextView.text = product.imageEmoji
        holder.nameTextView.text = product.name
        holder.quantityTextView.text = cartItem.quantity.toString()

        val totalItemPrice = cartItem.getTotalPrice()
        val formattedPrice = CurrencyFormatter.formatPrice(totalItemPrice, currentCountry)
        holder.priceTextView.text = formattedPrice

        holder.increaseButton.setOnClickListener {
            onQuantityChanged(product.id, cartItem.quantity + 1)
        }

        holder.decreaseButton.setOnClickListener {
            onQuantityChanged(product.id, cartItem.quantity - 1)
        }

        holder.removeButton.setOnClickListener {
            onRemove(product.id)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun updateCountry(country: String) {
        currentCountry = country
        notifyDataSetChanged()
    }
}