package com.example.skcamotes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ReservationPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment)  {

    override fun getItemCount(): Int = 3 // Number of tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AllReservationsFragment() // Replace with your fragment class
            1 -> ThirdFloorFragment() // Replace with your fragment class
            2 -> CalambaGymFragment() // Replace with your fragment class
            else -> AllReservationsFragment() // Fallback
        }
    }
}