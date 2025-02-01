package com.example.skcamotes.RequestFeature

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.skcamotes.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class RequestFormFragment : Fragment() {
    private lateinit var etSelectDate: EditText
    private lateinit var etSelectTime: EditText
    private lateinit var etNameOfItem: TextView
    private lateinit var btnSubmitForm: Button
    private lateinit var etPhoneNumber: EditText
    private lateinit var etQuantity: EditText
    private lateinit var etFullName: EditText
    private lateinit var etDescription: EditText
    private var selectedDrawable: Int? = null
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_request_form, container, false)

        // Initialize views
        etSelectDate = view.findViewById(R.id.etSelectDate)
        etSelectTime = view.findViewById(R.id.etSelectTime)
        etNameOfItem = view.findViewById(R.id.etNameOfItem)
        btnSubmitForm = view.findViewById(R.id.btnSubmitForm)
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber)
        etQuantity = view.findViewById(R.id.etQuantity)
        etFullName = view.findViewById(R.id.etFullName)
        etDescription = view.findViewById(R.id.etDescription)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("UserRequestedEquipments")

        // Retrieve arguments
        val itemName = arguments?.getString("item_name")
        selectedDrawable = arguments?.getInt("selected_drawable")

        // Set the item name
        etNameOfItem.text = itemName

        // Set up date and time pickers
        etSelectDate.setOnClickListener { showDatePicker() }
        etSelectTime.setOnClickListener { showTimePicker() }

        // Submit button click listener
        btnSubmitForm.setOnClickListener { fetchUserAndSaveDataToFirebase() }

        return view
    }

    private fun fetchUserAndSaveDataToFirebase() {
        val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(requireContext())

        val userEmail = when {
            firebaseUser != null -> firebaseUser.email // Get email from FirebaseAuth
            account != null -> account.email          // Get email from GoogleSignInAccount
            else -> null                              // No user logged in
        }

        if (userEmail != null) {
            saveDataToFirebase(userEmail)
        } else {
            Toast.makeText(requireContext(), "Unable to fetch user email. Please log in.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveDataToFirebase(userEmail: String) {
        val phoneNumber = etPhoneNumber.text.toString().trim()
        val quantity = etQuantity.text.toString().trim()
        val selectedDate = etSelectDate.text.toString().trim()
        val selectedTime = etSelectTime.text.toString().trim()
        val fullName = etFullName.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val itemName = etNameOfItem.text.toString().trim()

        if (phoneNumber.isBlank() || quantity.isBlank() || selectedDate.isBlank() || selectedTime.isBlank() || fullName.isBlank() || description.isBlank() || itemName.isBlank()) {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a unique ID for each request
        val requestId = database.push().key ?: return

        // Data to save
        val requestData = mapOf(
            "itemName" to itemName,
            "quantity" to quantity,
            "fullName" to fullName,
            "description" to description,
            "phoneNumber" to phoneNumber,
            "selectedDate" to selectedDate,
            "selectedTime" to selectedTime,
            "userEmail" to userEmail
        )

        // Save data to Firebase UserRequestedEquipments
        database.child(requestId).setValue(requestData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Request submitted successfully!", Toast.LENGTH_SHORT).show()
                // Navigate to ReceiptFragment or any other action
                navigateToReceiptFragment(itemName, phoneNumber, quantity, selectedDate, selectedTime)
            }
            .addOnFailureListener { error ->
                Log.e("FirebaseError", "Failed to save data: ${error.message}")
                Toast.makeText(requireContext(), "Failed to submit request. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToReceiptFragment(
        itemName: String,
        phoneNumber: String,
        quantity: String,
        selectedDate: String,
        selectedTime: String
    ) {
        val receiptFragment = ReceiptFragment()
        val bundle = Bundle()
        bundle.putString("item_name", itemName)
        bundle.putString("phone_number", phoneNumber)
        bundle.putString("quantity", quantity)
        bundle.putString("selected_date", selectedDate)
        bundle.putString("selected_time", selectedTime)
        selectedDrawable?.let { bundle.putInt("selected_drawable", it) }
        receiptFragment.arguments = bundle
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.wrapper, receiptFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            etSelectDate.setText(formattedDate)
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            etSelectTime.setText(formattedTime)
        }, hour, minute, true)
        timePickerDialog.show()
    }
}
