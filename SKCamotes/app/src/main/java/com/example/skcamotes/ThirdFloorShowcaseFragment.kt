package com.example.skcamotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ThirdFloorShowcaseFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for the fragment
        return inflater.inflate(R.layout.fragment_third_floor_showcase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the CheckBox by its ID and set up listener
        val heartRadioButton = view.findViewById<CheckBox>(R.id.heart_radio_button)
        heartRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up the MapView
        mapView = view.findViewById(R.id.thirdfloor_map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this) // This ensures that onMapReady will be called when the map is ready
    }

    // Implement OnMapReadyCallback interface to configure the map when ready
    override fun onMapReady(googleMap: GoogleMap) {
        // Set up a location for the marker (example: Calamba Gym coordinates)
        val calambaGymLocation = LatLng(14.2133, 121.1620) // Replace with your coordinates

        // Create a custom marker
        googleMap.addMarker(
            MarkerOptions()
                .position(calambaGymLocation)
                .title("Calamba Gym")
                .snippet("Fitness and Wellness Center")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_locations)) // Custom icon
        )

        // Move and zoom the camera to the marker
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
