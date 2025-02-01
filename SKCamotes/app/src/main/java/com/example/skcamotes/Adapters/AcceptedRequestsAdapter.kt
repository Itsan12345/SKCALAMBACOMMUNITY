package com.example.skcamotes.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skcamotes.R
import com.example.skcamotes.RequestFeature.UserRequestsDataClass

class AcceptedRequestsAdapter(
    private val items: List<UserRequestsDataClass>
) : RecyclerView.Adapter<AcceptedRequestsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fullNameTextView: TextView = view.findViewById(R.id.textView_fullName)
        val itemNameTextView: TextView = view.findViewById(R.id.textView_itemName)
        val quantityTextView: TextView = view.findViewById(R.id.textView_quantity)
        val dateTextView: TextView = view.findViewById(R.id.textView_date)
        val timeTextView: TextView = view.findViewById(R.id.textView_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_accepted_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.fullNameTextView.text = item.fullName
        holder.itemNameTextView.text = item.itemName
        holder.quantityTextView.text = item.quantity
        holder.dateTextView.text = item.selectedDate
        holder.timeTextView.text = item.selectedTime
    }

    override fun getItemCount(): Int = items.size
}
