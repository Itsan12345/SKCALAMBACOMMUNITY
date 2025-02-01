package com.example.skcamotes.RequestFeature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.skcamotes.R

class BasketballFragment : Fragment() {
    private var selectedDrawable: Int? = null
    private var selectedContainer: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_basketball, container, false)

        // List of container IDs and corresponding drawable resources
        val containerDetails = listOf(
            Triple(R.id.basketballContainer, R.drawable.basket_spalding, R.id.textViewBasketball),
            Triple(R.id.bussoContainer, R.drawable.basket_busso, R.id.textViewBusso),
            Triple(R.id.NiviaContainer, R.drawable.basket_nivia, R.id.textViewNivia),
            Triple(R.id.JumpshotContainer, R.drawable.basket_jumpshot, R.id.textViewJumpshot)
        )

        // Set click listeners for each container
        containerDetails.forEach { (containerId, drawable, textViewId) ->
            val container = view.findViewById<LinearLayout>(containerId)
            container.setOnClickListener {
                onContainerSelected(it)
                val selectedText = view.findViewById<TextView>(textViewId).text.toString()

                // Store the selected drawable and text
                selectedDrawable = drawable
                val bundle = Bundle()
                bundle.putInt("selected_drawable", selectedDrawable ?: R.drawable.default_image)
                bundle.putString("item_name", selectedText)

                // Pass the selected details to com.example.skcamotes.RequestFeature.RequestFormFragment
                val requestFormFragment = RequestFormFragment()
                requestFormFragment.arguments = bundle

                // Replace fragment with com.example.skcamotes.RequestFeature.RequestFormFragment
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.wrapper, requestFormFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        return view
    }

    private fun onContainerSelected(container: View) {
        selectedContainer?.setBackgroundResource(android.R.color.transparent)
        container.setBackgroundResource(R.drawable.selected_background)
        selectedContainer = container
    }
}
