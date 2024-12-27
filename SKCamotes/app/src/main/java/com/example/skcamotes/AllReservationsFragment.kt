package com.example.skcamotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class AllReservationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_reservations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the container view by its ID
        val calambaGymContainer = view.findViewById<View>(R.id.CalambaGymContainer)

        // Set up the click listener for Calamba Gym container
        calambaGymContainer.setOnClickListener {
            // Replace current fragment with CalambaGymShowcaseFragment
            val calambaGymShowcaseFragment = CalambaGymShowcaseFragment()
            replaceFragment(calambaGymShowcaseFragment)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.wrapper, fragment)  // R.id.wrapper is the container where the fragments are replaced
            addToBackStack(null)  // Add to back stack to allow going back to the previous fragment
            commit()
        }
    }
}
