package com.example.skcamotes

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.cardview.widget.CardView
import android.widget.LinearLayout

class EmergenciesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_emergencies, container, false)

        val etSearch: EditText = view.findViewById(R.id.et_search)
        val emergencyHotlines: LinearLayout = view.findViewById(R.id.emergency_hotlines)

        // List of all CardViews (emergency hotlines)
        val hotlines = listOf(
            view.findViewById<CardView>(R.id.pnpcalamba),
            view.findViewById<CardView>(R.id.bfpcalamba),
            view.findViewById<CardView>(R.id.cmccalamba),
            view.findViewById<CardView>(R.id.pamanacalamba),
            view.findViewById<CardView>(R.id.calambadoctors),
            view.findViewById<CardView>(R.id.jpcalamba)
        )

        val hotlineNames = listOf(
            "PNP Calamba",
            "BFP Calamba",
            "CMC Calamba",
            "Pama na Calamba",
            "Calamba Doctors",
            "JP Hospital"
        )

        // Search filter functionality
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().lowercase()
                for (i in hotlines.indices) {
                    val hotline = hotlines[i]
                    if (hotlineNames[i].lowercase().contains(searchText)) {
                        hotline.visibility = View.VISIBLE
                    } else {
                        hotline.visibility = View.GONE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Set click listeners for each hotline
        hotlines[0].setOnClickListener { replaceFragment(PNPCalambaFragment()) }
        hotlines[1].setOnClickListener { replaceFragment(BFPCalambaFragment()) }
        hotlines[2].setOnClickListener { replaceFragment(CMCFragment()) }
        hotlines[3].setOnClickListener { replaceFragment(PMCFragment()) }
        hotlines[4].setOnClickListener { replaceFragment(CalambaDoctorsFragment()) }
        hotlines[5].setOnClickListener { replaceFragment(JPHospitalFragment()) }

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
