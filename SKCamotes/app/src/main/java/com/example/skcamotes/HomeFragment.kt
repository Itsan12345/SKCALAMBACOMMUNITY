package com.example.skcamotes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private lateinit var carouselRecyclerView: RecyclerView
    private lateinit var btnLeftArrow: ImageButton
    private lateinit var btnRightArrow: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var layoutManager: CustomCarouselLayoutManager

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize FirebaseAuth and GoogleSignInClient
        auth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(
            requireContext(),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        )

        // Initialize the RecyclerView, arrows, and profile button
        carouselRecyclerView = view.findViewById(R.id.carouselRecyclerView)
        btnLeftArrow = view.findViewById(R.id.btnLeftArrow)
        btnRightArrow = view.findViewById(R.id.btnRightArrow)
        profileButton = view.findViewById(R.id.ic_Profile)

        // Define your image list
        val imageList = listOf(
            R.drawable.request_carouselbg,
            R.drawable.reservation_carouselbg,
            R.drawable.carousel_sample
        )

        // Set up the RecyclerView with the custom layout manager and adapter
        layoutManager = CustomCarouselLayoutManager(requireContext())
        carouselRecyclerView.layoutManager = layoutManager

        val adapter = CarouselAdapter(imageList)
        carouselRecyclerView.adapter = adapter

        // Scroll to the middle image
        val middlePosition = imageList.size / 2
        carouselRecyclerView.scrollToPosition(middlePosition)

        // Update arrow visibility on creation
        updateArrowVisibility()

        // Arrow button click listeners
        btnLeftArrow.setOnClickListener {
            val currentPosition = layoutManager.findFirstVisibleItemPosition()
            if (currentPosition > 0) {
                carouselRecyclerView.smoothScrollToPosition(currentPosition - 1)
                updateArrowVisibility(currentPosition - 1, imageList.size)
            }
        }

        btnRightArrow.setOnClickListener {
            val currentPosition = layoutManager.findFirstVisibleItemPosition()
            if (currentPosition < adapter.itemCount - 1) {
                carouselRecyclerView.smoothScrollToPosition(currentPosition + 1)
                updateArrowVisibility(currentPosition + 1, imageList.size)
            }
        }

        // Listen for scroll changes to update arrow visibility
        carouselRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                updateArrowVisibility()
            }
        })

        // Handle Profile button click for sign-out
        profileButton.setOnClickListener {
            showSignOutDialog()
        }

        return view
    }

    private fun updateArrowVisibility(currentPosition: Int? = null, itemCount: Int? = null) {
        val position = currentPosition ?: layoutManager.findFirstVisibleItemPosition()
        val totalItems = itemCount ?: (carouselRecyclerView.adapter?.itemCount ?: 0)

        btnLeftArrow.visibility = if (position > 0) View.VISIBLE else View.INVISIBLE
        btnRightArrow.visibility = if (position < totalItems - 1) View.VISIBLE else View.INVISIBLE
    }

    private fun showSignOutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { _, _ ->
                googleSignInClient.signOut().addOnCompleteListener {
                    auth.signOut()
                    val intent = Intent(requireContext(), LoginPage::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}
