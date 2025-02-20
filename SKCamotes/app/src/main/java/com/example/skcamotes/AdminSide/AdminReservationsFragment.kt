package com.example.skcamotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.skcamotes.databinding.FragmentAdminReservationsBinding
import com.google.android.material.tabs.TabLayoutMediator

class AdminReservationsFragment : Fragment() {

    private var _binding: FragmentAdminReservationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminReservationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up ViewPager with adapter
        binding.viewPager.adapter = ReservationsPagerAdapter(requireActivity())

        // Attach TabLayout with ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Pending"
                1 -> "Upcoming"
                2 -> "Completed"
                3 -> "Cancelled"
                else -> ""
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class ReservationsPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> TodaysReservationsFragment()
                1 -> UpcomingReservationsFragment()
                2 -> CompletedReservationsFragment()
                3 -> CancelledReservationsFragment()
                else -> throw IllegalStateException("Invalid position")
            }
        }
    }
}
