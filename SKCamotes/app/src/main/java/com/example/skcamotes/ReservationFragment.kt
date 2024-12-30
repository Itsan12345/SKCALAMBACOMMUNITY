package com.example.skcamotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2

class ReservationFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tvAll: TextView
    private lateinit var tvIndoor: TextView
    private lateinit var tvOutdoor: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reservations, container, false)

        // Initialize Views
        viewPager = view.findViewById(R.id.viewPager)
        tvAll = view.findViewById(R.id.tvAll)
        tvIndoor = view.findViewById(R.id.tvIndoor)
        tvOutdoor = view.findViewById(R.id.tvOutdoor)

        // Set up the ViewPager with a custom adapter
        val adapter = ReservationPagerAdapter(this)
        viewPager.adapter = adapter

        // Set click listeners for TextView filters
        tvAll.setOnClickListener {
            selectFilter(tvAll)
            viewPager.currentItem = 0
        }
        tvIndoor.setOnClickListener {
            selectFilter(tvIndoor)
            viewPager.currentItem = 1
        }
        tvOutdoor.setOnClickListener {
            selectFilter(tvOutdoor)
            viewPager.currentItem = 2
        }

        // Default selection
        selectFilter(tvAll)

        return view
    }

    private fun selectFilter(selectedTextView: TextView) {
        // Reset all filters to default style
        listOf(tvAll, tvIndoor, tvOutdoor).forEach {
            it.setTextColor(requireContext().getColor(R.color.gray))
            it.setBackgroundResource(R.drawable.unselected_filter_background)
        }

        // Highlight the selected filter
        selectedTextView.setTextColor(requireContext().getColor(android.R.color.white))
        selectedTextView.setBackgroundResource(R.drawable.selected_filter_background)
    }

}
