package com.example.skcamotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView

class EmergenciesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_emergencies, container, false)

        // Example: Button to replace the fragment
        val PNPcalamba: CardView = view.findViewById(R.id.pnpcalamba)
        PNPcalamba.setOnClickListener {
            // Replace with another fragment, e.g., ExampleFragment
            replaceFragment(PNPCalambaFragment())
        }

        val BFPcalamba: CardView = view.findViewById(R.id.bfpcalamba)
        BFPcalamba.setOnClickListener {
            // Replace with another fragment, e.g., ExampleFragment
            replaceFragment(BFPCalambaFragment())
        }

        val CMCCalamba: CardView = view.findViewById(R.id.cmccalamba)
        CMCCalamba.setOnClickListener {
            // Replace with another fragment, e.g., ExampleFragment
            replaceFragment(CMCFragment())
        }

        val PMCCalamba: CardView = view.findViewById(R.id.pamanacalamba)
        PMCCalamba.setOnClickListener {
            // Replace with another fragment, e.g., ExampleFragment
            replaceFragment(PMCFragment())
        }

        val CalambaDoctors: CardView = view.findViewById(R.id.calambadoctors)
        CalambaDoctors.setOnClickListener {
            // Replace with another fragment, e.g., ExampleFragment
            replaceFragment(CalambaDoctorsFragment())
        }

        val JPHospital: CardView = view.findViewById(R.id.jpcalamba)
        JPHospital.setOnClickListener {
            // Replace with another fragment, e.g., ExampleFragment
            replaceFragment(JPHospitalFragment())
        }


        return view
    }

    // Method to replace the current fragment
    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.wrapper, fragment) // Ensure R.id.wrapper exists in the activity layout
            addToBackStack(null) // Allow going back to the previous fragment
            commit()
        }
    }
}
