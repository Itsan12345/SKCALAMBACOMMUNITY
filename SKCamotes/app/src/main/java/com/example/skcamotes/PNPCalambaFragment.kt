package com.example.skcamotes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class PNPCalambaFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for the fragment
        return inflater.inflate(R.layout.fragment_p_n_p_calamba, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the MapView
        mapView = view.findViewById(R.id.pnp_map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this) // This ensures that onMapReady will be called when the map is ready

        // Handle back button
        val btnBack: ImageButton = view.findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Call button functionality
        val callButton: ImageView = view.findViewById(R.id.call_button)
        val phoneNumberTextView: TextView = view.findViewById(R.id.textView22)

        callButton.setOnClickListener {
            val phoneNumber = phoneNumberTextView.text.toString()
            if (phoneNumber.isNotEmpty()) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val callIntent = Intent(Intent.ACTION_CALL).apply {
                        data = Uri.parse("tel:$phoneNumber")
                    }
                    startActivity(callIntent)
                } else {
                    // Request CALL_PHONE permission if not granted
                    requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 101)
                }
            } else {
                Toast.makeText(context, "Phone number is not available", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permission granted. Please try calling again.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permission denied. Cannot make calls.", Toast.LENGTH_SHORT).show()
        }
    }

    // Implement OnMapReadyCallback interface to configure the map when ready
    override fun onMapReady(googleMap: GoogleMap) {
        // Set up a location for the marker (example: Calamba Gym coordinates)
        val calambaPNPLocation = LatLng(14.214704356185313, 121.11093850657156) // Replace with your coordinates
        // Create a custom marker
        googleMap.addMarker(
            MarkerOptions()
                .position(calambaPNPLocation)
                .title("PNP CALAMBA")
                .snippet("We are your Philippine National Police")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pinpoint)) // Custom icon
        )

        // Move and zoom the camera to the marker
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(calambaPNPLocation, 17f))
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
