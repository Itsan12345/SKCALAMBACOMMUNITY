package com.example.skcamotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SportsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sports, container, false)

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.setSelectedTabIndicator(0)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

        // Set up the ViewPager2 with the adapter
        val adapter = SportsPagerAdapter(requireActivity())
        viewPager.adapter = adapter

        // Link TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Basketball"
                1 -> "Volleyball"
                else -> "Tab"
            }
        }.attach()

        return view
    }
}
