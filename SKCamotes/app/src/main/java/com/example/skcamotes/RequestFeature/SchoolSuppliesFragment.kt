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

class SchoolSuppliesFragment : Fragment() {
    private var selectedDrawable: Int? = null
    private var selectedItemName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_school_supplies, container, false)

        // Find RecyclerViews
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewEquipments0)
        val recyclerView2: RecyclerView = view.findViewById(R.id.recyclerViewEquipments1)

        // Sample data for first RecyclerView
        val equipmentList = listOf(
            Equipment("Ballpoint Pens", R.drawable.img_ballpen),
            Equipment("Pencils", R.drawable.img_pencil),
            Equipment("Glue", R.drawable.img_glue),
            Equipment("Highlighters", R.drawable.img_highlighters),
            Equipment("Notebooks", R.drawable.img_notebook),
            Equipment("Paint Sets", R.drawable.img_paintsets),
            Equipment("Geometry Sets", R.drawable.img_geometryset),
            Equipment("Scissors", R.drawable.img_scissor)
        )

        // Sample data for second RecyclerView
        val equipmentList2 = listOf(
            Equipment("Chalk", R.drawable.img_chalk),
            Equipment("Markers", R.drawable.img_marker),
            Equipment("Eraser", R.drawable.img_eraser),
            Equipment("Stapler", R.drawable.img_stapler)
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
