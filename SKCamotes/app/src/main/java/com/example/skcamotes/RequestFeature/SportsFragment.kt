package com.example.skcamotes.RequestFeature

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skcamotes.R

class   SportsFragment : Fragment() {
    private var selectedDrawable: Int? = null
    private var selectedItemName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sports, container, false)

        // Find RecyclerViews
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewEquipmentsSports1)
        val recyclerView2: RecyclerView = view.findViewById(R.id.recyclerViewEquipmentsSports2)

        // Sample data for first RecyclerView
        val equipmentList = listOf(
            Equipment("Spalding", R.drawable.basket_spalding),
            Equipment("Busso", R.drawable.basket_busso),
            Equipment("Nivia", R.drawable.basket_nivia),
            Equipment("Jumpshot", R.drawable.basket_jumpshot)
        )

        // Sample data for second RecyclerView
        val equipmentList2 = listOf(
            Equipment("Molten", R.drawable.molten_vb),
            Equipment("Fiv3", R.drawable.fiv3_vb),
            Equipment("Star", R.drawable.star_vb),
            Equipment("Mikasa", R.drawable.mikasa_vb)
        )

        // Handle back button
        val btnBack: Button = view.findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Set up first RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = EquipmentsAdapter(equipmentList) { equipment ->
            onItemSelected(equipment)
        }

        // Set up second RecyclerView
        recyclerView2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView2.adapter = EquipmentsAdapter(equipmentList2) { equipment ->
            onItemSelected(equipment)
        }

        return view
    }

    // Handle item click to pass data to next fragment
    private fun onItemSelected(equipment: Equipment) {
        selectedDrawable = equipment.imageResId
        selectedItemName = equipment.name

        // Prepare data to pass to com.example.skcamotes.RequestFeature.RequestFormFragment
        val bundle = Bundle().apply {
            putInt("selected_drawable", selectedDrawable ?: R.drawable.default_image)
            putString("item_name", selectedItemName)
        }

        // Create and navigate to com.example.skcamotes.RequestFeature.RequestFormFragment
        val requestFormFragment = RequestFormFragment().apply {
            arguments = bundle
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.wrapper, requestFormFragment)
            .addToBackStack(null)
            .commit()
    }
}
