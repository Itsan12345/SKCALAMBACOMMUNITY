package com.example.skcamotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class SchoolSuppliesFragment : Fragment() {
    private var selectedDrawable: Int? = null
    private var selectedContainer: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_school_supplies, container, false)

        // List of container IDs and corresponding drawable resources
        val containerDetails = listOf(
            Triple(R.id.card_pencil, R.drawable.img_pencil, R.id.textViewPencil),
            Triple(R.id.card_bondpaper, R.drawable.img_bondpaper, R.id.textViewBondpaper),
            Triple(R.id.card_notebook, R.drawable.img_notebook, R.id.textViewNotebook),
            Triple(R.id.card_ballpen, R.drawable.img_ballpen, R.id.textViewBallpen)
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

                // Pass the selected details to RequestFormFragment
                val requestFormFragment = RequestFormFragment()
                requestFormFragment.arguments = bundle

                // Replace fragment with RequestFormFragment
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
