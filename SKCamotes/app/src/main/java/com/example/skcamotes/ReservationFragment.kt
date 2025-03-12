package com.example.skcamotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2

class ReservationFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tvAll: TextView
    private lateinit var tvIndoor: TextView
    private lateinit var tvOutdoor: TextView
    private lateinit var icHistory: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reservations, container, false)

        // Initialize Views
        viewPager = view.findViewById(R.id.viewPager)
        tvAll = view.findViewById(R.id.tvAll)
        tvIndoor = view.findViewById(R.id.tvIndoor)
        tvOutdoor = view.findViewById(R.id.tvOutdoor)
        icHistory = view.findViewById(R.id.icHistory)

        val adapter = ReservationPagerAdapter(this)
        viewPager.adapter = adapter

        // Set click listeners for TextView filters
        tvAll.setOnClickListener {
            viewPager.currentItem = 0
        }
        tvIndoor.setOnClickListener {
            viewPager.currentItem = 1
        }
        tvOutdoor.setOnClickListener {
            viewPager.currentItem = 2
        }

        // Sync filter highlight with page changes
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> selectFilter(tvAll)
                    1 -> selectFilter(tvIndoor)
                    2 -> selectFilter(tvOutdoor)
                }
            }
        })

        // Handle click for icHistory to replace the fragment
        icHistory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.wrapper, Reservations_MyRoomsFragment())
                .addToBackStack(null)
                .commit()
        }

        // Default selection
        selectFilter(tvAll)

        return view
    }

    private fun selectFilter(selectedTextView: TextView) {
        listOf(tvAll, tvIndoor, tvOutdoor).forEach {
            it.setTextColor(requireContext().getColor(R.color.gray))
            it.setBackgroundResource(R.drawable.unselected_filter_background)
        }

        selectedTextView.setTextColor(requireContext().getColor(android.R.color.white))
        selectedTextView.setBackgroundResource(R.drawable.selected_filter_background)
    }
}
