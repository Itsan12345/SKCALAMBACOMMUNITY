package com.example.skcamotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ThirdFloorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_third_floor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the container views by their IDs
        val thirdFloorContainer = view.findViewById<View>(R.id.ThirdFloorContainer)

        // Set up the click listener for Third Floor container
        thirdFloorContainer.setOnClickListener {
            // Replace current fragment with ThirdFloorShowcaseFragment
            val thirdFloorShowcaseFragment = ThirdFloorShowcaseFragment()
            replaceFragment(thirdFloorShowcaseFragment)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.wrapper, fragment) // R.id.wrapper is the container where the fragments are replaced
            addToBackStack(null) // Add to back stack to allow going back to the previous fragment
            commit()
        }
    }
}
