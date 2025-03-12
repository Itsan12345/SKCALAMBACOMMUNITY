package com.example.skcamotes.Adapters

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.skcamotes.AdminSide.AdminAcceptedRequestDataClass
import com.example.skcamotes.R
import com.google.firebase.database.*

class AdminAcceptedRequestAdapter(private val requestList: MutableList<AdminAcceptedRequestDataClass>) :
    RecyclerView.Adapter<AdminAcceptedRequestAdapter.ViewHolder>() {

    private val database =
        FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val acceptedRequestsRef = database.getReference("AcceptedRequests")
    private val returnedRequestsRef = database.getReference("ReturnedRequests")
    private val lostRequestsRef =
        database.getReference("LostRequests") // Added LostRequests reference

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userEmail: TextView = itemView.findViewById(R.id.txtUserEmail)
        val itemName: TextView = itemView.findViewById(R.id.txtItemName)
        val quantity: TextView = itemView.findViewById(R.id.txtQuantity)
        val selectedDate: TextView = itemView.findViewById(R.id.txtSelectedDate)
        val selectedTime: TextView = itemView.findViewById(R.id.txtSelectedTime)

        val btnReturned: Button = itemView.findViewById(R.id.btnReturned)
        val btnLost: Button = itemView.findViewById(R.id.btnLost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.admin_item_accepted_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requestList[position]
        holder.userEmail.text = request.userEmail
        holder.itemName.text = "Item Name: ${request.itemName}"
        holder.quantity.text = "Quantity: ${request.quantity}"
        holder.selectedDate.text = "Date: ${request.selectedDate}"
        holder.selectedTime.text = "Time: ${request.selectedTime}"

        holder.btnReturned.visibility = View.GONE
        holder.btnLost.visibility = View.GONE

        holder.itemView.setOnClickListener {
            if (holder.btnReturned.visibility == View.GONE) {
                holder.btnReturned.visibility = View.VISIBLE
                holder.btnLost.visibility = View.VISIBLE
            } else {
                holder.btnReturned.visibility = View.GONE
                holder.btnLost.visibility = View.GONE
            }
        }

        holder.btnReturned.setOnClickListener {
            moveToReturned(request, holder)
        }

        holder.btnLost.setOnClickListener {
            moveToLost(request, holder) // Handle moving to LostRequests
        }
    }

    override fun getItemCount(): Int = requestList.size

    private fun moveToReturned(request: AdminAcceptedRequestDataClass, holder: ViewHolder) {
        acceptedRequestsRef.orderByChild("userEmail").equalTo(request.userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (requestSnapshot in snapshot.children) {
                        val requestId = requestSnapshot.key ?: continue

                        // Move data to "ReturnedRequests"
                        returnedRequestsRef.child(requestId).setValue(request)
                            .addOnSuccessListener {
                                // Delete from "AcceptedRequests"
                                acceptedRequestsRef.child(requestId).removeValue()
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            holder.itemView.context,
                                            "Moved to ReturnedRequests",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // Send a Thank You email
                                        sendThankYouEmail(
                                            holder.itemView.context,
                                            request.userEmail,
                                            request.itemName
                                        )
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            holder.itemView.context,
                                            "Failed to delete from AcceptedRequests",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    holder.itemView.context,
                                    "Failed to move data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        holder.itemView.context,
                        "Database error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // Function to send an email
    private fun sendThankYouEmail(context: Context, userEmail: String, itemName: String) {
        val subject = "Thank You for Returning the Item!"
        val message =
            "Dear User,\n\nThank you for returning the item: $itemName. We appreciate your honesty and responsibility.\n\nBest regards,\nAdmin Team"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(userEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Send Email via..."))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveToLost(request: AdminAcceptedRequestDataClass, holder: ViewHolder) {
        acceptedRequestsRef.orderByChild("userEmail").equalTo(request.userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (requestSnapshot in snapshot.children) {
                        val requestId = requestSnapshot.key ?: continue

                        // Move data to "LostRequests"
                        lostRequestsRef.child(requestId).setValue(request)
                            .addOnSuccessListener {
                                // Delete from "AcceptedRequests"
                                acceptedRequestsRef.child(requestId).removeValue()
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            holder.itemView.context,
                                            "Moved to LostRequests",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // Send email notification about lost item payment
                                        sendLostItemEmail(
                                            holder.itemView.context,
                                            request.userEmail,
                                            request.itemName
                                        )
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            holder.itemView.context,
                                            "Failed to delete from AcceptedRequests",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    holder.itemView.context,
                                    "Failed to move data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        holder.itemView.context,
                        "Database error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // Function to send an email notification about lost item payment
    private fun sendLostItemEmail(context: Context, userEmail: String, itemName: String) {
        val subject = "Important: Payment Required for Lost Item"
        val message = """
        Dear User,

        We would like to inform you that the item "$itemName" has not been returned within the required timeframe of three (3) days past the designated deadline. In accordance with our policies, you are now required to settle the corresponding payment for the lost item.
        
        Kindly visit the Finance Office of Barangay Calamba at your earliest convenience to obtain details regarding the outstanding amount and payment procedures.
        
        Should you have any questions or require further assistance, please do not hesitate to contact us.
        
        Thank you for your prompt attention to this matter.
        
        Sincerely,  
        Barangay Calamba Administration
    """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(userEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Send Email via..."))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }
}