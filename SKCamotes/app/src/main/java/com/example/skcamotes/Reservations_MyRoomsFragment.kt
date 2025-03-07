package com.example.skcamotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.skcamotes.databinding.FragmentReservationsMyRoomsBinding
import com.google.android.material.tabs.TabLayoutMediator

class Reservations_MyRoomsFragment : Fragment() {

    private var _binding: FragmentReservationsMyRoomsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservationsMyRoomsBinding.inflate(inflater, container, false)

        // Set up ViewPager2 with Adapter
        val adapter = MyRoomsPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Link TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "PENDING"
                1 -> "PAID"
                2 -> "CANCELLED"
                else -> ""
            }
        }.attach()

        // Handle Back Button Click
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Adapter for ViewPager2
class MyRoomsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Reservations_PendingFragment()
            1 -> Reservations_PaidFragment()
            2 -> Reservations_CancelledFragment()
            else -> Fragment()
        }
    }
}
