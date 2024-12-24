package com.example.skcamotes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SportsPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2 // Two tabs: Basketball and Volleyball

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BasketballFragment()
            1 -> VolleyballFragment()
            else -> BasketballFragment()
        }
    }
}
