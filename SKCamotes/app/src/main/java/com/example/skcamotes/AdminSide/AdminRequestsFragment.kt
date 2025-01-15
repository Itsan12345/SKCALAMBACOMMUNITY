package com.example.skcamotes.AdminSide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            // On CardView click, show the requested equipment for the user
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
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(AdminRequestDataClass::class.java)
                    user?.let { userList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // Method to fetch and show the requested equipment for the user
    private fun showRequestedEquipmentDialog(user: AdminRequestDataClass) {
        val userEquipmentsRef = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("UserRequestedEquipments") // Firebase path for the requested equipment

        userEquipmentsRef.orderByChild("userEmail").equalTo(user.email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val requestedEquipments = mutableListOf<String>()
                for (equipmentSnapshot in snapshot.children) {
                    val equipment = equipmentSnapshot.getValue(UserRequestedEquipment::class.java)
                    equipment?.let {
                        // Fetch all necessary information
                        val description = equipment.description ?: "N/A"
                        val email = equipment.userEmail ?: "N/A"
                        val fullName = equipment.fullName ?: "N/A"
                        val itemName = equipment.itemName ?: "N/A"
                        val phoneNumber = equipment.phoneNumber ?: "N/A"
                        val quantity = equipment.quantity ?: "N/A"
                        val selectedDate = equipment.selectedDate ?: "N/A"
                        val selectedTime = equipment.selectedTime ?: "N/A"

                        // Combine the information
                        requestedEquipments.add(
                            """
                                Description: $description
                                Email: $email
                                Full Name: $fullName
                                Item Name: $itemName
                                Phone Number: $phoneNumber
                                Quantity: $quantity
                                Selected Date: $selectedDate
                                Selected Time: $selectedTime
                            """.trimIndent()
                        )
                    }
                }

                // Show the equipment in a dialog
                showDialogWithRequestedEquipmentDetails(requestedEquipments)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun showDialogWithRequestedEquipmentDetails(equipments: List<String>) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Requested Equipment")

        // Display the requested equipment details
        val equipmentDetails = equipments.joinToString("\n\n")
        builder.setMessage(equipmentDetails.ifEmpty { "No requested equipment found." })

        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
