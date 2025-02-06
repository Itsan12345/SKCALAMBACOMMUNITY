package com.example.p2_handson_rolloque

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.p2_handson_rolloque.models.User

class UserAdapter(private val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFirstName: TextView = itemView.findViewById(R.id.tvFirstName)
        val tvLastName: TextView = itemView.findViewById(R.id.tvLastName)
        val tvMiddleName: TextView = itemView.findViewById(R.id.tvMiddleName)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvAge: TextView = itemView.findViewById(R.id.tvAge)
        val tvDOB: TextView = itemView.findViewById(R.id.tvDOB)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        val btnUpdate: Button = itemView.findViewById(R.id.btnUpdate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        // Set values to the TextViews
        holder.tvFirstName.text = "First Name: ${user.firstName}"
        holder.tvLastName.text = "Last Name: ${user.lastName}"
        holder.tvMiddleName.text = "Middle Name: ${user.middleName}"
        holder.tvEmail.text = "Email: ${user.email}"
        holder.tvAge.text = "Age: ${user.age}"
        holder.tvDOB.text = "Date of Birth: ${user.dateOfBirth}"

        // Set up the delete button
        holder.btnDelete.setOnClickListener {
            val userId = userList[position].firstName // Replace with unique ID
            (holder.itemView.context as UsersListActivity).deleteUser(userId)
        }

        // Set up the update button
        holder.btnUpdate.setOnClickListener {
            val userId = userList[position].firstName // Replace with unique ID
            (holder.itemView.context as UsersListActivity).editUser(userId)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
