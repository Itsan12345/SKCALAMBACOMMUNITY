package com.example.skcamotes.AdminSide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skcamotes.databinding.ItemAdminRequestBinding

class AdminRequestsAdapter(
    private val users: List<AdminRequestDataClass>,
    private val onCardClick: (AdminRequestDataClass) -> Unit // Lambda to handle click events
) : RecyclerView.Adapter<AdminRequestsAdapter.AdminRequestsViewHolder>() {

    class AdminRequestsViewHolder(val binding: ItemAdminRequestBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminRequestsViewHolder {
        val binding = ItemAdminRequestBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AdminRequestsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminRequestsViewHolder, position: Int) {
        val user = users[position]
        holder.binding.txtName.text = user.name
        holder.binding.txtEmail.text = user.email
        holder.binding.txtPhone.text = user.phone

        // Show notification badge if there's a new request
        if (user.hasNewRequest) {
            holder.binding.notificationBadge.visibility = View.VISIBLE
        } else {
            holder.binding.notificationBadge.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onCardClick(user)
        }
    }

    override fun getItemCount() = users.size
}