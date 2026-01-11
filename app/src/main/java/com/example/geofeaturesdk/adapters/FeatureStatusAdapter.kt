package com.example.geofeaturesdk.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.geofeaturesdk.R
import com.example.geofeaturesdk.models.FeatureStatus
import com.google.android.material.card.MaterialCardView


class FeatureStatusAdapter(
    private var features: List<FeatureStatus>
) : RecyclerView.Adapter<FeatureStatusAdapter.FeatureViewHolder>() {

    inner class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView.findViewById(R.id.featureCard)
        val iconTextView: TextView = itemView.findViewById(R.id.featureIcon)
        val nameTextView: TextView = itemView.findViewById(R.id.featureName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.featureDescription)
        val statusTextView: TextView = itemView.findViewById(R.id.featureStatus)
        val valueTextView: TextView = itemView.findViewById(R.id.featureValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feature_status, parent, false)
        return FeatureViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {
        val feature = features[position]

        holder.iconTextView.text = feature.getIcon()
        holder.nameTextView.text = feature.name.replace("_", " ").capitalize()
        holder.descriptionTextView.text = feature.getDescription()

        if (feature.enabled) {
            holder.statusTextView.text = "✅ Enabled"
            holder.statusTextView.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
            holder.card.strokeColor = holder.itemView.context.getColor(android.R.color.holo_green_light)
        } else {
            holder.statusTextView.text = "❌ Disabled"
            holder.statusTextView.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
            holder.card.strokeColor = holder.itemView.context.getColor(android.R.color.holo_red_light)
        }

        if (feature.value != null) {
            holder.valueTextView.visibility = View.VISIBLE
            holder.valueTextView.text = "Value: ${feature.value}"
        } else {
            holder.valueTextView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = features.size

    fun updateFeatures(newFeatures: List<FeatureStatus>) {
        features = newFeatures
        notifyDataSetChanged()
    }
}