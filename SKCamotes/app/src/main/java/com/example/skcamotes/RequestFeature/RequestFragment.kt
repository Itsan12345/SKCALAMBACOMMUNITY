package com.example.skcamotes.RequestFeature

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.skcamotes.R

class RequestFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_requests, container, false)

        // Find the card_sports CardView and set an OnClickListener
        val cardSports: LinearLayout = view.findViewById(R.id.card_sports)
        cardSports.setOnClickListener {
            listener?.onCardSportsClicked() // Notify the listener
        }

        // Find the card_schoolsupp CardView and set an OnClickListener
        val cardSchoolSupplies: LinearLayout = view.findViewById(R.id.card_schoolsupp)
        cardSchoolSupplies.setOnClickListener {
            listener?.onCardSchoolSuppliesClicked() // Notify the listener
        }

        // Find the card_equipments CardView and set an OnClickListener
        val cardEquipments: LinearLayout = view.findViewById(R.id.card_equipments)
        cardEquipments.setOnClickListener {
            listener?.onCardEquipmentsClicked() // Notify the listener
        }

        // Handle card_requests click to navigate to RequestActivity
        val cardRequests: LinearLayout = view.findViewById(R.id.card_requests)
        cardRequests.setOnClickListener {
            val intent = Intent(requireContext(), RequestActivity::class.java)
            startActivity(intent)
        }


        return view
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    // Interface for fragment interaction
    interface OnFragmentInteractionListener {
        fun onCardSportsClicked()
        fun onCardSchoolSuppliesClicked() // New method
        fun onCardEquipmentsClicked()

    }
}
