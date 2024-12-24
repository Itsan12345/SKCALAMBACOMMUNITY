package com.example.skcamotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Equipment(val name: String, val imageResId: Int)

class EquipmentsAdapter(
    private val equipmentList: List<Equipment>,
    private val onItemClick: (Equipment) -> Unit // Lambda to handle click event
) : RecyclerView.Adapter<EquipmentsAdapter.EquipmentViewHolder>() {

    class EquipmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgEquipment: ImageView = itemView.findViewById(R.id.imgEquipment)
        val tvEquipmentName: TextView = itemView.findViewById(R.id.tvEquipmentName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_equipment, parent, false)
        return EquipmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: EquipmentViewHolder, position: Int) {
        val equipment = equipmentList[position]
        holder.imgEquipment.setImageResource(equipment.imageResId)
        holder.tvEquipmentName.text = equipment.name

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick(equipment) // Trigger the click event
        }
    }

    override fun getItemCount(): Int = equipmentList.size
}
