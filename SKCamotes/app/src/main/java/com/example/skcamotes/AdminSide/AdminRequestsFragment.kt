package com.example.skcamotes.AdminSide

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skcamotes.R
import com.example.skcamotes.databinding.FragmentAdminRequestsBinding
import com.google.firebase.database.*

class AdminRequestsFragment : Fragment() {

    private var _binding: FragmentAdminRequestsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var adapter: AdminRequestsAdapter
    private val userList = mutableListOf<AdminRequestDataClass>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Users")

        adapter = AdminRequestsAdapter(userList) { user ->
            showRequestedEquipmentDialog(user)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        fetchUsers()
    }

    private fun fetchUsers() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val tempUserList = mutableListOf<AdminRequestDataClass>()
                val userRequestsRef = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("UserRequestedEquipments")

                // Fetch all users
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(AdminRequestDataClass::class.java)
                    if (user != null && user.name.isNotBlank() && user.email.isNotBlank() && user.phone.isNotBlank()) {
                        tempUserList.add(user)
                    }
                }

                // Check if each user has a request
                userRequestsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(requestSnapshot: DataSnapshot) {
                        for (user in tempUserList) {
                            var hasRequest = false
                            for (request in requestSnapshot.children) {
                                val requestEmail = request.child("userEmail").getValue(String::class.java)
                                if (requestEmail == user.email) {
                                    hasRequest = true
                                    break
                                }
                            }
                            user.hasNewRequest = hasRequest
                        }

                        userList.clear()
                        userList.addAll(tempUserList)
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun showRequestedEquipmentDialog(user: AdminRequestDataClass) {
        val userEquipmentsRef = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("UserRequestedEquipments")

        userEquipmentsRef.orderByChild("userEmail").equalTo(user.email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val equipmentViews = mutableListOf<View>()
                    for (equipmentSnapshot in snapshot.children) {
                        val equipment = equipmentSnapshot.getValue(UserRequestedEquipment::class.java)
                        equipment?.let {
                            val view = createEquipmentView(equipment, user)
                            equipmentViews.add(view)
                        }
                    }
                    showEquipmentDetailsDialog(user, equipmentViews)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun createEquipmentView(equipment: UserRequestedEquipment, user: AdminRequestDataClass): View {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.equipment_item_layout, null)

        view.findViewById<TextView>(R.id.tvItemName).text = equipment.itemName ?: "N/A"
        view.findViewById<TextView>(R.id.tvQuantity).text = equipment.quantity ?: "N/A"
        view.findViewById<TextView>(R.id.tvFullName).text = equipment.fullName ?: "N/A"
        view.findViewById<TextView>(R.id.tvDescription).text = equipment.description ?: "N/A"
        view.findViewById<TextView>(R.id.tvPhoneNumber).text = equipment.phoneNumber ?: "N/A"
        view.findViewById<TextView>(R.id.tvDate).text = "${equipment.selectedDate ?: "N/A"}"
        view.findViewById<TextView>(R.id.tvTime).text = "${equipment.selectedTime ?: "N/A"}"

        view.findViewById<Button>(R.id.btnAccept).setOnClickListener {
            acceptEquipmentRequest(equipment, user)
        }
        view.findViewById<Button>(R.id.btnReject).setOnClickListener {
            rejectEquipmentRequest(equipment, user)
        }

        return view
    }

    private fun showEquipmentDetailsDialog(user: AdminRequestDataClass, equipmentViews: List<View>) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Requested Equipment")

        val container = LinearLayout(requireContext())
        container.orientation = LinearLayout.VERTICAL
        for (equipmentView in equipmentViews) {
            container.addView(equipmentView)
        }

        builder.setView(container)
        builder.setPositiveButton("Close") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun acceptEquipmentRequest(equipment: UserRequestedEquipment, user: AdminRequestDataClass) {
        val acceptedRequestsRef = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("AcceptedRequests")

        acceptedRequestsRef.push().setValue(equipment)
            .addOnSuccessListener {
                removeRequestFromDatabase(equipment)
                sendEmailNotification(user.email, equipment.itemName)
                showSuccessDialog("Equipment request accepted successfully, and an email has been sent to ${user.email}.")
            }
            .addOnFailureListener {
                showErrorDialog("Failed to accept the equipment request. Please try again.")
            }
    }


    private fun sendEmailNotification(userEmail: String, itemName: String) {
        val subject = "Your Equipment Request Has Been Accepted"
        val message = "Hello,\n\nYour request for the equipment '$itemName' has been accepted. Please coordinate for further details.\n\nThank you!"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(userEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        try {
            startActivity(Intent.createChooser(intent, "Send Email"))
        } catch (e: Exception) {
            showErrorDialog("No email app found. Please install an email client.")
        }
    }

    private fun rejectEquipmentRequest(equipment: UserRequestedEquipment, user: AdminRequestDataClass) {
        val rejectionReasons = arrayOf("Out of Stock", "Invalid Description", "Other")
        var selectedReason = rejectionReasons[0]

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Rejection Reason")
        builder.setSingleChoiceItems(rejectionReasons, 0) { _, which ->
            selectedReason = rejectionReasons[which]
        }

        builder.setPositiveButton("Reject") { _, _ ->
            val rejectedRequestsRef = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("RejectedRequests")

            rejectedRequestsRef.push().setValue(equipment)
                .addOnSuccessListener {
                    removeRequestFromDatabase(equipment)
                    sendRejectionEmail(user.email, equipment.itemName, selectedReason)
                    showSuccessDialog("Equipment request rejected successfully, and an email has been sent to ${user.email}.")
                }
                .addOnFailureListener {
                    showErrorDialog("Failed to reject the equipment request. Please try again.")
                }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun sendRejectionEmail(userEmail: String, itemName: String, reason: String) {
        val subject = "Your Equipment Request Has Been Rejected"
        val message = "Hello,\n\nYour request for the equipment '$itemName' has been rejected due to the following reason: $reason.\n\nIf you have any questions, please contact us.\n\nThank you."

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(userEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        try {
            startActivity(Intent.createChooser(intent, "Send Email"))
        } catch (e: Exception) {
            showErrorDialog("No email app found. Please install an email client.")
        }
    }

    private fun removeRequestFromDatabase(equipment: UserRequestedEquipment) {
        val userEquipmentsRef = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("UserRequestedEquipments")

        userEquipmentsRef.orderByChild("userEmail").equalTo(equipment.userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (equipmentSnapshot in snapshot.children) {
                        val currentEquipment = equipmentSnapshot.getValue(UserRequestedEquipment::class.java)
                        if (currentEquipment?.itemName == equipment.itemName &&
                            currentEquipment.quantity == equipment.quantity &&
                            currentEquipment.selectedDate == equipment.selectedDate &&
                            currentEquipment.selectedTime == equipment.selectedTime &&
                            currentEquipment.userEmail == equipment.userEmail
                        ) {
                            equipmentSnapshot.ref.removeValue()
                                .addOnSuccessListener {

                                }
                                .addOnFailureListener {
                                    showErrorDialog("Failed to remove the request. Please try again.")
                                }
                            break
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showErrorDialog("Failed to access the database. Please try again later.")
                }
            })
    }

    private fun showSuccessDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Success")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}