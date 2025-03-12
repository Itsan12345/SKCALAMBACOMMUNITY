package com.example.skcamotes.AdminSide

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.skcamotes.Adapters.AdminLostFragment
import com.example.skcamotes.Adapters.AdminReturnedFragments

class AdminRequestedItemsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AdminAcceptedFragment()
            1 -> AdminReturnedFragments()
            2 -> AdminLostFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}
