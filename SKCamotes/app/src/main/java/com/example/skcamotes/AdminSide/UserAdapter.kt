package com.example.skcamotes

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skcamotes.AdminSide.User

class UserAdapter(private val userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.user_name)
        val email: TextView = view.findViewById(R.id.user_email)
        val phone: TextView = view.findViewById(R.id.user_phone)
        val role: TextView = view.findViewById(R.id.user_role)
        val timestamp: TextView = view.findViewById(R.id.user_timestamp) // Timestamp view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_login, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.name.text = user.name
        holder.email.text = user.email
        holder.phone.text = user.phone
        holder.role.text = user.role

        // Format the timestamp and display it
        val formattedTimestamp = DateFormat.format("MM/dd/yyyy hh:mm a", user.timestamp)
        holder.timestamp.text = formattedTimestamp
    }

    override fun getItemCount() = userList.size
}
