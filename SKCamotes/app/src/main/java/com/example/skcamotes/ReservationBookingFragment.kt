package com.example.skcamotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import android.widget.CalendarView
import java.text.SimpleDateFormat
import java.util.*

class ReservationBookingFragment : Fragment() {

    private lateinit var dateRangeDisplay: TextView
    private lateinit var decreaseGuests: ImageButton
    private lateinit var increaseGuests: ImageButton
    private lateinit var guestsCountText: TextView
    private lateinit var paymentMethodGroup: RadioGroup
    private lateinit var totalPriceText: TextView
    private lateinit var bookNowButton: Button
    private lateinit var calendarView: CalendarView

    private var guestsCount = 0
    private val basePricePerGuest = 150
    private var totalPrice = basePricePerGuest * guestsCount

    private var startDate: Long? = null
    private var endDate: Long? = null
    private var firstClickTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reservation_booking, container, false)

        dateRangeDisplay = view.findViewById(R.id.date_range_display)
        decreaseGuests = view.findViewById(R.id.decrease_guests)
        increaseGuests = view.findViewById(R.id.increase_guests)
        guestsCountText = view.findViewById(R.id.guests_count)
        paymentMethodGroup = view.findViewById(R.id.payment_methods)
        totalPriceText = view.findViewById(R.id.total_price)
        bookNowButton = view.findViewById(R.id.book_now_button)
        calendarView = view.findViewById(R.id.calendar_view)

        // Retrieve the passed gym title
        val gymTitle = arguments?.getString("GYM_TITLE") ?: "Default Subtitle"


        // Find the Toolbar (or any element with app:subtitle)
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.subtitle = gymTitle


        updateGuestsAndPrice()

        // Date picker (for range)
        view.findViewById<TextView>(R.id.select_booking_dates).setOnClickListener {
            showDateRangePicker()
        }

        // Guest count buttons
        decreaseGuests.setOnClickListener {
            if (guestsCount > 1) {
                guestsCount--
                updateGuestsAndPrice()
            }
        }

        increaseGuests.setOnClickListener {
            guestsCount++
            updateGuestsAndPrice()
        }

        // Booking button
        bookNowButton.setOnClickListener {
            // Get the subtitle from the toolbar
            val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            val gymTitle = toolbar.subtitle.toString() // Get the subtitle text

            // Get the selected date range text
            val dateRange = dateRangeDisplay.text.toString()

            // Replace the current fragment with ReservationReceiptFragment and pass the subtitle
            val transaction = parentFragmentManager.beginTransaction()
            val reservationReceiptFragment = ReservationReceiptFragment()

            // Pass the necessary data (guestsCount, gymTitle, dateRange, and totalPrice) as arguments
            val bundle = Bundle()
            bundle.putString("GYM_TITLE", gymTitle)
            bundle.putString("DATE_RANGE", dateRange)
            bundle.putInt("GUESTS_COUNT", guestsCount)  // Pass the guestsCount value here
            bundle.putString("TOTAL_PRICE", totalPriceText.text.toString())  // Pass the total price
            reservationReceiptFragment.arguments = bundle

            transaction.replace(R.id.fragment_container, reservationReceiptFragment)
            transaction.addToBackStack(null) // Optional: adds the transaction to back stack
            transaction.commit()

            val selectedPaymentMethod = view.findViewById<RadioButton>(paymentMethodGroup.checkedRadioButtonId)?.text
                ?: "No Payment Method Selected"
            showBookingSummary(selectedPaymentMethod)
        }




        // CalendarView for selecting start and end date
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.timeInMillis

            if (firstClickTime == 0L) {
                firstClickTime = System.currentTimeMillis()
                startDate = selectedDate
                // Display single selected date
                dateRangeDisplay.text = "Start Date: ${formatDate(startDate)}"
            } else {
                // Check if clicked within a short time for second click
                if (System.currentTimeMillis() - firstClickTime < 1000) {
                    endDate = selectedDate
                    // Update text to show both start and end date
                    dateRangeDisplay.text = "From: ${formatDate(startDate)}\nTo: ${formatDate(endDate)}"
                    firstClickTime = 0L // Reset for next selection
                } else {
                    startDate = selectedDate
                    // Display only the new selected start date
                    dateRangeDisplay.text = "Start Date: ${formatDate(startDate)}"
                    firstClickTime = System.currentTimeMillis()
                }
            }
        }

        return view
    }

    private fun updateGuestsAndPrice() {
        guestsCountText.text = guestsCount.toString()
        totalPrice = guestsCount * basePricePerGuest
        totalPriceText.text = "₱$totalPrice"
    }

    private fun showDateRangePicker() {
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now()) // Ensure dates are forward

        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Booking Dates")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        dateRangePicker.show(parentFragmentManager, "date_picker")

        dateRangePicker.addOnPositiveButtonClickListener { dateRange ->
            startDate = dateRange.first
            endDate = dateRange.second

            val formattedStartDate = formatDate(startDate)
            val formattedEndDate = formatDate(endDate)

            // Update the TextView with the selected date range
            dateRangeDisplay.text = "From: $formattedStartDate\nTo: $formattedEndDate"
        }
    }

    private fun formatDate(timestamp: Long?): String {
        if (timestamp == null) return "--"
        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun showBookingSummary(paymentMethod: CharSequence) {
        println("Booking Summary: Guests: $guestsCount, Total Price: ₱$totalPrice, Payment: $paymentMethod")
    }
}
