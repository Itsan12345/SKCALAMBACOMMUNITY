package com.example.skcamotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CalambaGymShowcaseFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for the fragment
        return inflater.inflate(R.layout.fragment_calamba_gym_showcase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle back button click
        val btnBack = view.findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            requireActivity().onBackPressed() // Navigates to the previous page
        }

        // Set up the MapView
        mapView = view.findViewById(R.id.gym_map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this) // This ensures that onMapReady will be called when the map is ready

        // Find the "book now" button and set up the click listener
        val bookNowButton = view.findViewById<View>(R.id.book_now_button)
        bookNowButton.setOnClickListener {
            val gymTitleText = view.findViewById<TextView>(R.id.gym_title).text.toString()

            // Create a new instance of ReservationBookingFragment and pass the gym title as an argument
            val reservationBookingFragment = ReservationBookingFragment().apply {
                arguments = Bundle().apply {
                    putString("GYM_TITLE", gymTitleText)
                }
            }
            replaceFragment(reservationBookingFragment)
        }
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

    // Method to replace the current fragment
    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.wrapper, fragment) // R.id.wrapper is the container where the fragments are replaced
            addToBackStack(null) // Add to back stack to allow going back to the previous fragment
            commit()
        }
    }
}
