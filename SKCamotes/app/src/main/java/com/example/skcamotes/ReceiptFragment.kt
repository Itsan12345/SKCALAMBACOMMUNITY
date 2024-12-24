package com.example.skcamotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ReceiptFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var textView9: TextView
    private lateinit var textView11: TextView
    private lateinit var quantityText: TextView
    private lateinit var textViewDate: TextView
    private lateinit var textViewTime: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_receipt, container, false)

        imageView = view.findViewById(R.id.imageViewInConstraintLayout2) // ImageView inside ConstraintLayout2
        textView9 = view.findViewById(R.id.textView9)
        textView11 = view.findViewById(R.id.textView11)
        quantityText = view.findViewById(R.id.QuantityText)
        textViewDate = view.findViewById(R.id.textView15)
        textViewTime = view.findViewById(R.id.textView13)

        // Retrieve arguments from previous fragment
        val itemName = arguments?.getString("item_name") ?: "No item name provided"
        val phoneNumber = arguments?.getString("phone_number") ?: "No phone number provided"
        val quantity = arguments?.getString("quantity") ?: "No quantity provided"
        val selectedDate = arguments?.getString("selected_date") ?: "No date selected"
        val selectedTime = arguments?.getString("selected_time") ?: "No time selected"
        val selectedDrawable = arguments?.getInt("selected_drawable")

        // Set values in TextViews
        textView9.text = itemName
        textView11.text = phoneNumber
        quantityText.text = quantity
        textViewDate.text = selectedDate
        textViewTime.text = selectedTime

        // Set the image resource
        if (selectedDrawable != null) {
            imageView.setImageResource(selectedDrawable)
        } else {
            imageView.setImageResource(R.drawable.default_image) // Default image if no drawable passed
            Toast.makeText(requireContext(), "No image selected, using default image.", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
