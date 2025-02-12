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
        val today = CalendarDay.today() // Get current date
        startDate = today // Set startDate to today by default
        updateDateRangeDisplay()

        // Set minimum date to today to prevent past date selection
        calendarView.state().edit()
            .setMinimumDate(today) // Disallow past dates
            .commit()

        calendarView.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                if (date.isBefore(today)) {
                    return@setOnDateChangedListener // Ignore past dates
                }

                if (startDate == null) {
                    startDate = date
                    endDate = null
                } else if (endDate == null && date.isAfter(startDate!!)) {
                    endDate = date
                } else {
                    startDate = date
                    endDate = null
                }
                updateDateRangeDisplay()
                updateCalendarDecorator()
            }
        }

        // Update calendar decorations
        updateCalendarDecorator()
    }

    private fun updateDateRangeDisplay() {
        val fromText = startDate?.let { "From: ${formatDate(it.date)}" } ?: "From: -"
        val toText = endDate?.let { " To: ${formatDate(it.date)}" } ?: " To: -"
        dateRangeDisplay.text = "$fromText$toText"
    }

    private fun updateCalendarDecorator() {
        calendarView.removeDecorators()
        if (startDate != null && endDate != null) {
            calendarView.addDecorator(CustomDateDecorator(startDate, endDate))
        }
        calendarView.invalidate()
    }

    private fun updateGuestsAndPrice() {
        guestsCountText.text = guestsCount.toString()
        totalPrice = guestsCount * basePricePerGuest
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


    // Custom Decorator for Highlighting Selected Dates
    class CustomDateDecorator(
        private val startDate: CalendarDay?,
        private val endDate: CalendarDay?
    ) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.isInRange(startDate, endDate)
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.WHITE)) // Change text color
            view.addSpan(StyleSpan(Typeface.BOLD)) // Make text bold
            view.setSelectionDrawable(ColorDrawable(Color.parseColor("#9510D3"))) // Change background color
        }
    }
}
