package com.example.skcamotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MyReservationsBrgyCalamba : Fragment(), OnMapReadyCallback { // Implement OnMapReadyCallback

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_reservations_brgy_calamba, container, false)

        // Initialize the MapView
        mapView = view.findViewById(R.id.gym_map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this) // Ensures onMapReady() is triggered

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle back button click
        val btnBack = view.findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map // Store the map instance

        // Set up location for the marker
        val calambaGymLocation = LatLng(14.2133, 121.1620) // Replace with actual coordinates

        // Add a marker with a valid icon
        googleMap.addMarker(
            MarkerOptions()
                .position(calambaGymLocation)
                .title("Calamba Gym")
                .snippet("Fitness and Wellness Center")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)) // Use default marker for now
        )

        // Move camera to marker
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(calambaGymLocation, 15f))
    }

    // Lifecycle methods to manage MapView
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
