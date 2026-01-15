package com.example.geofeaturesdk

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geofeaturesdk.adapters.CartAdapter
import com.example.geofeaturesdk.models.ShoppingCart
import com.example.geofeaturesdk.utils.CurrencyFormatter
import com.example.geofeaturesdk.utils.GeoHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView


class CheckoutActivity : AppCompatActivity() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var paymentMethodsChipGroup: ChipGroup
    private lateinit var subtotalTextView: MaterialTextView
    private lateinit var discountTextView: MaterialTextView
    private lateinit var totalTextView: MaterialTextView
    private lateinit var placeOrderButton: MaterialButton

    private lateinit var cartAdapter: CartAdapter

    private var currentCountry = "US"
    private var selectedPaymentMethod: String? = null
    private var discountPercent = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "🛒 Checkout"

        initViews()
        loadDataFromAPI()
    }

    private fun initViews() {
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        paymentMethodsChipGroup = findViewById(R.id.paymentMethodsChipGroup)
        subtotalTextView = findViewById(R.id.subtotalTextView)
        discountTextView = findViewById(R.id.discountTextView)
        totalTextView = findViewById(R.id.totalTextView)
        placeOrderButton = findViewById(R.id.placeOrderButton)

        // Cart RecyclerView
        cartAdapter = CartAdapter(
            items = ShoppingCart.getItems(),
            currentCountry = currentCountry,
            onQuantityChanged = { productId, newQuantity ->
                if (newQuantity <= 0) {
                    ShoppingCart.removeItem(productId)
                } else {
                    ShoppingCart.updateQuantity(productId, newQuantity)
                }
                updatePrices()
            },
            onRemove = { productId ->
                ShoppingCart.removeItem(productId)
                updatePrices()
                if (ShoppingCart.getItemCount() == 0) {
                    finish()
                }
            }
        )
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = cartAdapter

        placeOrderButton.setOnClickListener {
            placeOrder()
        }
    }


    private fun loadDataFromAPI() {
        GeoHelper.getCurrentCountry(this) { country ->
            currentCountry = country
            runOnUiThread {
                cartAdapter.updateCountry(country)
                loadPaymentMethods()
                checkDiscount()
            }
        }
    }


    private fun loadPaymentMethods() {
        GeoHelper.isFeatureEnabled(this, "payment_methods") { enabled, value ->
            runOnUiThread {
                if (enabled && value != null) {
                    val methods = value.split(",").map { it.trim() }

                    paymentMethodsChipGroup.removeAllViews()

                    methods.forEachIndexed { index, methodId ->
                        val chip = Chip(this)
                        chip.text = formatPaymentMethod(methodId)
                        chip.isCheckable = true
                        chip.isChecked = index == 0

                        chip.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                selectedPaymentMethod = methodId
                            }
                        }

                        paymentMethodsChipGroup.addView(chip)
                    }

                    selectedPaymentMethod = methods.firstOrNull()
                } else {
                    Toast.makeText(this, "No payment methods available", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun checkDiscount() {
        GeoHelper.isFeatureEnabled(this, "black_friday_discount") { enabled, value ->
            runOnUiThread {
                if (enabled && value != null) {
                    discountPercent = value.toIntOrNull() ?: 0

                    if (discountPercent > 0) {
                        discountTextView.visibility = View.VISIBLE
                    } else {
                        discountTextView.visibility = View.GONE
                    }
                } else {
                    discountPercent = 0
                    discountTextView.visibility = View.GONE
                }

                updatePrices()
            }
        }
    }


    private fun updatePrices() {
        val subtotal = ShoppingCart.getTotalPrice()
        val discount = subtotal * (discountPercent / 100.0)
        val total = subtotal - discount

        subtotalTextView.text = "Subtotal: ${CurrencyFormatter.formatPrice(subtotal, currentCountry)}"

        if (discountPercent > 0) {
            discountTextView.text = "🎉 Black Friday ($discountPercent% OFF): -${CurrencyFormatter.formatPrice(discount, currentCountry)}"
        }

        totalTextView.text = "Total: ${CurrencyFormatter.formatPrice(total, currentCountry)}"

        cartAdapter.updateItems(ShoppingCart.getItems())
    }


    private fun formatPaymentMethod(methodId: String): String {
        return when (methodId.lowercase()) {
            "credit_card" -> "💳 Credit Card"
            "paypal" -> "💰 PayPal"
            "bit" -> "📱 Bit"
            "apple_pay" -> "🍎 Apple Pay"
            "google_pay" -> "🔵 Google Pay"
            else -> "💳 ${methodId.replace("_", " ").capitalize()}"
        }
    }


    private fun placeOrder() {
        if (selectedPaymentMethod == null) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
            return
        }

        if (ShoppingCart.getItemCount() == 0) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val subtotal = ShoppingCart.getTotalPrice()
        val discount = subtotal * (discountPercent / 100.0)
        val total = subtotal - discount
        val formattedTotal = CurrencyFormatter.formatPrice(total, currentCountry)

        val discountText = if (discountPercent > 0) {
            "\n🎉 You saved: ${CurrencyFormatter.formatPrice(discount, currentCountry)}"
        } else ""

        Toast.makeText(
            this,
            "✅ Order placed!\nTotal: $formattedTotal\nPayment: ${formatPaymentMethod(selectedPaymentMethod!!)}$discountText",
            Toast.LENGTH_LONG
        ).show()

        ShoppingCart.clear()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}