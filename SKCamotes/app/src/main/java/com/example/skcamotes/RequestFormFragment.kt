package com.example.skcamotes

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import java.util.*

class RequestFormFragment : Fragment() {

    private lateinit var etSelectDate: EditText
    private lateinit var etSelectTime: EditText
    private lateinit var etNameOfItem: TextView
    private lateinit var btnSubmitForm: Button
    private lateinit var etPhoneNumber: EditText
    private lateinit var etQuantity: EditText

    private var selectedDrawable: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_request_form, container, false)

        etSelectDate = view.findViewById(R.id.etSelectDate)
        etSelectTime = view.findViewById(R.id.etSelectTime)
        etNameOfItem = view.findViewById(R.id.etNameOfItem)
        btnSubmitForm = view.findViewById(R.id.btnSubmitForm)
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber)
        etQuantity = view.findViewById(R.id.etQuantity)

        // Retrieve arguments
        val itemName = arguments?.getString("item_name")
        selectedDrawable = arguments?.getInt("selected_drawable")

        // Set the item name
        etNameOfItem.text = itemName

        // Set up date and time pickers
        etSelectDate.setOnClickListener { showDatePicker() }
        etSelectTime.setOnClickListener { showTimePicker() }

        // Submit button click listener
        btnSubmitForm.setOnClickListener {
            val phoneNumber = etPhoneNumber.text.toString()
            val quantity = etQuantity.text.toString()
            val selectedDate = etSelectDate.text.toString()
            val selectedTime = etSelectTime.text.toString()

            if (phoneNumber.isNotBlank() && quantity.isNotBlank() && selectedDate.isNotBlank() && selectedTime.isNotBlank()) {
                // Pass values to ReceiptFragment
                val receiptFragment = ReceiptFragment()
                val bundle = Bundle()
                bundle.putString("item_name", itemName)
                bundle.putString("phone_number", phoneNumber)
                bundle.putString("quantity", quantity)
                bundle.putString("selected_date", selectedDate)
                bundle.putString("selected_time", selectedTime)
                selectedDrawable?.let {
                    bundle.putInt("selected_drawable", it)
                }
                receiptFragment.arguments = bundle

                // Replace with ReceiptFragment
                val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.wrapper, receiptFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            } else {
                // Show error message if any field is empty
                Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
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
