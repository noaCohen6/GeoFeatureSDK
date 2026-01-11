package com.example.geofeaturesdk.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.geofeaturesdk.R
import com.example.geofeaturesdk.models.Product
import com.example.geofeaturesdk.utils.CurrencyFormatter
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView


class ProductAdapter(
    private var products: List<Product>,
    private var currentCountry: String,
    private val onAddToCart: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView.findViewById(R.id.productCard)
        val emojiTextView: TextView = itemView.findViewById(R.id.productEmoji)
        val nameTextView: TextView = itemView.findViewById(R.id.productName)
        val priceTextView: TextView = itemView.findViewById(R.id.productPrice)
        val addButton: MaterialButton = itemView.findViewById(R.id.addToCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.emojiTextView.text = product.imageEmoji
        holder.nameTextView.text = product.name

        val formattedPrice = CurrencyFormatter.formatPrice(product.basePrice, currentCountry)
        holder.priceTextView.text = formattedPrice

        holder.addButton.setOnClickListener {
            onAddToCart(product)
        }

        holder.card.setOnClickListener {
        }
    }

    override fun getItemCount(): Int = products.size


    fun updateProducts(newProducts: List<Product>, country: String) {
        products = newProducts
        currentCountry = country
        notifyDataSetChanged()
    }


    fun updateCountry(country: String) {
        currentCountry = country
        notifyDataSetChanged()
    }
}