package com.example.skcamotes.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skcamotes.AdminSide.AdminLostRequestDataClass
import com.example.skcamotes.R

class AdminLostRequestAdapter(private val returnedRequests: List<AdminLostRequestDataClass>) :
    RecyclerView.Adapter<AdminLostRequestAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUserEmail: TextView = itemView.findViewById(R.id.txtUserEmail)
        val txtItemName: TextView = itemView.findViewById(R.id.txtItemName)
        val txtQuantity: TextView = itemView.findViewById(R.id.txtQuantity)
        val txtSelectedDate: TextView = itemView.findViewById(R.id.txtSelectedDate)
        val txtSelectedTime: TextView = itemView.findViewById(R.id.txtSelectedTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.admin_item_lost_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = returnedRequests[position]
        holder.txtUserEmail.text = request.userEmail
        holder.txtItemName.text = "Item Name: ${request.itemName}"
        holder.txtQuantity.text = "Quantity: ${request.quantity}"
        holder.txtSelectedDate.text = "Date: ${request.selectedDate}"
        holder.txtSelectedTime.text = "Time: ${request.selectedTime}"
    }

    override fun getItemCount(): Int {
        return returnedRequests.size
    }
}
