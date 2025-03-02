package com.example.skcamotes

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.widget.Toolbar
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
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
    private lateinit var calendarView: MaterialCalendarView
    private lateinit var toolbar: Toolbar

    private var guestsCount = 1
    private val basePricePerGuest = 50
    private var totalPrice = basePricePerGuest * guestsCount

    private var startDate: CalendarDay? = null
    private var endDate: CalendarDay? = null

    private lateinit var database: DatabaseReference
    private val bookedDates = mutableListOf<CalendarDay>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reservation_booking, container, false)

        dateRangeDisplay = view.findViewById(R.id.date_range_display)
        decreaseGuests = view.findViewById(R.id.decrease_guests)
        increaseGuests = view.findViewById(R.id.increase_guests)
        guestsCountText = view.findViewById(R.id.guests_count)
        totalPriceText = view.findViewById(R.id.total_price)
        bookNowButton = view.findViewById(R.id.book_now_button)
        calendarView = view.findViewById(R.id.calendar_view)
        toolbar = view.findViewById(R.id.toolbar) // Find the toolbar
        val paymentMethodGroup = view.findViewById<RadioGroup>(R.id.payment_methods)
        val radioCOD = view.findViewById<RadioButton>(R.id.radio_cod)
        val radioGCash = view.findViewById<RadioButton>(R.id.radio_gcash)

        // Set toolbar navigation icon and handle back navigation
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_back, null)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed() // Go back to the previous activity/screen
        }

        // Initialize Firebase Database Reference
        database = FirebaseDatabase.getInstance(
            "https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("reservationreceipt")

        // Handle payment selection
        paymentMethodGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_cod -> {
                    showToast("You selected COD. Please pay in cash in Barangay Calamba Finance.")
                }

                R.id.radio_gcash -> {
                    showGcashPaymentDialog()
                }
            }
        }

        // Retrieve the passed gym title
        val gymTitle = arguments?.getString("GYM_TITLE") ?: "Default Subtitle"
        toolbar.subtitle = gymTitle // Set subtitle

        updateGuestsAndPrice()
        setupCalendarView()
        fetchBookedDates()

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
            if (startDate != null && endDate != null) {
                val selectedRadioButtonId = paymentMethodGroup.checkedRadioButtonId
                val selectedPaymentMethod = view.findViewById<RadioButton>(selectedRadioButtonId)?.text ?: "No Payment Method Selected"

                showBookingSummary(selectedPaymentMethod)
                goToReservationReceiptFragment(selectedPaymentMethod.toString())
            } else {
                dateRangeDisplay.text = "Please select dates"
            }
        }

        return view
    }

    private fun fetchBookedDates() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookedDates.clear()
                for (reservation in snapshot.children) {
                    val dateString = reservation.child("date").getValue(String::class.java)
                    Log.d("FirebaseData", "Retrieved date: $dateString") // Log retrieved date

                    if (dateString != null) {
                        val dateList = parseDate(dateString) // Get list of dates
                        if (dateList != null) {
                            for (date in dateList) {
                                val calendarDay = CalendarDay.from(date)
                                bookedDates.add(calendarDay)
                                Log.d("ParsedDate", "Successfully parsed date: $calendarDay") // Log parsed date
                            }
                        } else {
                            Log.e("ParsedDateError", "Failed to parse date: $dateString")
                        }
                    }
                }
                updateCalendarDecorator()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Failed to load booked dates: ${error.message}")
                Toast.makeText(requireContext(), "Failed to load booked dates", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun parseDate(dateString: String): List<Date>? {
        return try {
            val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

            // Split the date range by " - "
            val dates = dateString.split(" - ")

            // Check if we have both start and end dates
            if (dates.size == 2) {
                val startDate = sdf.parse(dates[0].trim()) // Parse the first date
                val endDate = sdf.parse(dates[1].trim())   // Parse the second date

                Log.d("DateParsing", "Parsed start date: $startDate, end date: $endDate") // Debugging log

                if (startDate != null && endDate != null) {
                    // Generate a list of all dates in the range
                    generateDateRange(startDate, endDate)
                } else {
                    Log.e("DateParsingError", "Start or end date is null")
                    null
                }
            } else {
                Log.e("DateParsingError", "Invalid date format: $dateString")
                null
            }
        } catch (e: Exception) {
            Log.e("DateParsingError", "Error parsing date: $dateString", e)
            null
        }
    }

    private fun generateDateRange(startDate: Date, endDate: Date): List<Date> {
        val datesInRange = mutableListOf<Date>()
        val calendar = Calendar.getInstance()

        calendar.time = startDate

        while (!calendar.time.after(endDate)) {
            datesInRange.add(calendar.time)
            calendar.add(Calendar.DATE, 1) // Move to the next day
        }

        return datesInRange
    }



    // Show GCash Payment Dialog
    private fun showGcashPaymentDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("GCash Payment")
        alertDialog.setMessage("Proceed to GCash payment?")
        alertDialog.setPositiveButton("Pay Now") { _, _ ->
            paywithGcash()
        }
        alertDialog.setNegativeButton("Cancel", null)
        alertDialog.show()
    }

    private fun paywithGcash() {
        val checkoutUrl = "https://pm.link/org-ethanrolloquepaymongo/test/fJCSXCi"
        openCheckoutPage(checkoutUrl)
    }

    private fun openCheckoutPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }


    // Helper function to show toast messages
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setupCalendarView() {
        val today = CalendarDay.today()
        calendarView.state().edit()
            .setMinimumDate(today)
            .commit()

        calendarView.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                if (bookedDates.contains(date)) {
                    Toast.makeText(requireContext(), "Date is already booked!", Toast.LENGTH_SHORT).show()
                    return@setOnDateChangedListener
                }

                if (startDate == null) {
                    startDate = date
                    endDate = null
                } else if (endDate == null && date.isAfter(startDate!!)) {
                    // Check if the selected range includes booked dates
                    val selectedRange = getDatesBetween(startDate!!.date, date.date)
                    if (selectedRange.any { bookedDates.contains(it) }) {
                        Toast.makeText(requireContext(), "Selected range includes booked dates!", Toast.LENGTH_SHORT).show()
                        startDate = null
                        endDate = null
                    } else {
                        endDate = date
                    }
                } else {
                    startDate = date
                    endDate = null
                }
                updateDateRangeDisplay()
                updateCalendarDecorator()
            }
        }
        updateCalendarDecorator()
    }

    // Helper function to get all dates between two dates
    private fun getDatesBetween(startDate: Date, endDate: Date): List<CalendarDay> {
        val dates = mutableListOf<CalendarDay>()
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        while (calendar.time.before(endDate) || calendar.time == endDate) {
            dates.add(CalendarDay.from(calendar))
            calendar.add(Calendar.DATE, 1)
        }
        return dates
    }

    private fun updateDateRangeDisplay() {
        val fromText = startDate?.let { " ${formatDate(it.date)}" } ?: "From: -"
        val toText = endDate?.let { " - ${formatDate(it.date)}" } ?: " To: -"
        dateRangeDisplay.text = "$fromText$toText"
        updateGuestsAndPrice()
    }

    private fun updateCalendarDecorator() {
        calendarView.removeDecorators()
        if (startDate != null && endDate != null) {
            calendarView.addDecorator(SelectedDateDecorator(startDate, endDate))
        }
        if (bookedDates.isNotEmpty()) {
            calendarView.addDecorator(BookedDatesDecorator(bookedDates))
        }
        calendarView.invalidate()
    }

    private fun updateGuestsAndPrice() {
        guestsCountText.text = guestsCount.toString()

        // Calculate the number of reserved days
        val numberOfDaysReserved = if (startDate != null && endDate != null) {
            val startDateMillis = startDate!!.date.time
            val endDateMillis = endDate!!.date.time
            val diffInMillis = endDateMillis - startDateMillis
            TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS).toInt() + 1
        } else {
            1 // Default to 1 day if no date range is selected
        }

        // Update total price formula: guests * number of days reserved
        totalPrice = guestsCount * numberOfDaysReserved * basePricePerGuest
        totalPriceText.text = "₱$totalPrice"
    }


    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    private fun showBookingSummary(paymentMethod: CharSequence) {
        println("Booking Summary: Guests: $guestsCount, Total Price: ₱$totalPrice, Payment: $paymentMethod")
    }

    private fun goToReservationReceiptFragment(paymentMethod: String) {
        val intent = Intent(requireContext(), ReservationReceiptActivity::class.java).apply {
            putExtra("GYM_TITLE", toolbar.subtitle.toString())
            putExtra("DATE_RANGE", dateRangeDisplay.text.toString())
            putExtra("GUESTS_COUNT", guestsCount)
            putExtra("TOTAL_PRICE", totalPriceText.text.toString())
            putExtra("PAYMENT_METHOD", paymentMethod) // Pass the selected payment method
        }
        startActivity(intent)
    }

    // Decorator for Selected Dates
    class SelectedDateDecorator(
        private val startDate: CalendarDay?,
        private val endDate: CalendarDay?
    ) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.isInRange(startDate, endDate)
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.WHITE))
            view.addSpan(StyleSpan(Typeface.BOLD))
            view.setSelectionDrawable(ColorDrawable(Color.parseColor("#9510D3")))
        }
    }

    // Decorator for Booked Dates (Disables them)
    class BookedDatesDecorator(private val bookedDates: List<CalendarDay>) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return bookedDates.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            view.setDaysDisabled(true)
            view.addSpan(ForegroundColorSpan(Color.RED)) // Show booked dates in red
        }
    }


}
