package com.example.skcamotes.RequestFeature

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skcamotes.R

class EquipmentsFragment : Fragment() {
    private var selectedDrawable: Int? = null
    private var selectedItemName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_equipments, container, false)

        // Find RecyclerViews
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewEquipments)
        val recyclerView2: RecyclerView = view.findViewById(R.id.recyclerViewEquipments2)

        // Sample data for first RecyclerView
        val equipmentList = listOf(
            Equipment("Chairs", R.drawable.img_chairr),
            Equipment("Tables", R.drawable.img_table),
            Equipment("Projector", R.drawable.img_projector),
            Equipment("Sound System", R.drawable.img_soundsystems)
        )

        // Sample data for second RecyclerView
        val equipmentList2 = listOf(
            Equipment("Hammers", R.drawable.img_hammer),
            Equipment("Ladder", R.drawable.img_ladder),
            Equipment("Drills", R.drawable.img_drill),
            Equipment("Saws", R.drawable.img_saw)
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
