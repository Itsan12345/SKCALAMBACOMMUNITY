package com.example.skcamotes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.skcamotes.AdminSide.Announcement
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.skcamotes.RequestFeature.RequestFragment

class HomeFragment : Fragment() {

    private lateinit var announcementsRecyclerView: RecyclerView
    private lateinit var announcementsAdapter: HomeAnnouncementsAdapter
    private lateinit var databaseReference: DatabaseReference

    private lateinit var carouselRecyclerView: RecyclerView
    private lateinit var btnLeftArrow: ImageButton
    private lateinit var btnRightArrow: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var layoutManager: CustomCarouselLayoutManager
    private lateinit var tabDots: TabLayout // TabLayout for dots
    private lateinit var searchEditText: EditText
    private var originalAnnouncementsList = mutableListOf<Announcement>()
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val handler = Handler(Looper.getMainLooper())
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            val currentPosition = layoutManager.findFirstVisibleItemPosition()
            val totalItems = carouselRecyclerView.adapter?.itemCount ?: 0

            if (totalItems > 0) {
                val nextPosition = if (currentPosition < totalItems - 1) {
                    currentPosition + 1
                } else {
                    0 // Restart from the first image
                }

                carouselRecyclerView.smoothScrollToPosition(nextPosition)
                tabDots.selectTab(tabDots.getTabAt(nextPosition))
            }

            handler.postDelayed(this, 5000) // Auto-scroll every 5 seconds
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        searchEditText = view.findViewById(R.id.et_search)

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterAnnouncements(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Initialize FirebaseAuth and GoogleSignInClient
        auth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(
            requireContext(),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        )

        // Announcements RecyclerView
        announcementsRecyclerView = view.findViewById(R.id.announcements_recyclerview)
        announcementsRecyclerView.layoutManager = LinearLayoutManager(context)
        announcementsAdapter = HomeAnnouncementsAdapter()
        announcementsRecyclerView.adapter = announcementsAdapter

        // Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance(
            "https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).reference.child("announcements")

        // Fetch announcements from Firebase
        fetchAnnouncements()

        // Carousel setup
        carouselRecyclerView = view.findViewById(R.id.carouselRecyclerView)
        btnLeftArrow = view.findViewById(R.id.btnLeftArrow)
        btnRightArrow = view.findViewById(R.id.btnRightArrow)
        profileButton = view.findViewById(R.id.ic_Profile)
        tabDots = view.findViewById(R.id.tabDots) // Initialize TabLayout for dots

        val imageList = listOf(
            R.drawable.request_carouselbg,
            R.drawable.reservation_carouselbg,
            R.drawable.emergency_carouselbg
        )

        layoutManager = CustomCarouselLayoutManager(requireContext())
        carouselRecyclerView.layoutManager = layoutManager

        val carouselAdapter = CarouselAdapter(imageList) { imageResId ->
            if (imageResId == R.drawable.request_carouselbg) {
                navigateToRequestFragment()
            }

            if (imageResId == R.drawable.reservation_carouselbg) {
                navigateToReservationFragment()
            }

            if (imageResId == R.drawable.emergency_carouselbg) {
                navigateToEmergencyFragment()
            }

        }

        carouselRecyclerView.adapter = carouselAdapter

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(carouselRecyclerView)

        // Scroll to the middle image initially
        val middlePosition = imageList.size / 2
        carouselRecyclerView.scrollToPosition(middlePosition)
        updateArrowVisibility(middlePosition, imageList.size)

        // Add TabLayout dots dynamically
        for (i in imageList.indices) {
            tabDots.addTab(tabDots.newTab())
        }

        // Start auto-scroll
        handler.postDelayed(autoScrollRunnable, 5000)

        // Add gap between dots
        addGapBetweenDots(tabDots)

        // Synchronize dots with RecyclerView scrolling
        carouselRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val currentPosition = layoutManager.findFirstVisibleItemPosition()
                    tabDots.selectTab(tabDots.getTabAt(currentPosition))
                }
            }
        })

        // Synchronize RecyclerView with dots
        tabDots.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { position ->
                    carouselRecyclerView.smoothScrollToPosition(position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Arrow click listeners
        btnLeftArrow.setOnClickListener {
            val currentPosition = layoutManager.findFirstVisibleItemPosition()
            if (currentPosition > 0) {
                carouselRecyclerView.smoothScrollToPosition(currentPosition - 1)
                updateArrowVisibility(currentPosition - 1, imageList.size)
            }
        }

        btnRightArrow.setOnClickListener {
            val currentPosition = layoutManager.findFirstVisibleItemPosition()
            if (currentPosition < carouselAdapter.itemCount - 1) {
                carouselRecyclerView.smoothScrollToPosition(currentPosition + 1)
                updateArrowVisibility(currentPosition + 1, imageList.size)
            }
        }

        // RecyclerView scroll listener for dynamic arrow visibility
        carouselRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val currentPosition = layoutManager.findFirstVisibleItemPosition()
                    updateArrowVisibility(currentPosition, imageList.size)
                }
            }
        })

        // Profile button click listener
        profileButton.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        return view
    }


    private fun navigateToEmergencyFragment() {
        (activity as? MainActivity)?.navigateToFragment3(EmergenciesFragment())
    }

    private fun navigateToReservationFragment() {
        (activity as? MainActivity)?.navigateToFragment2(ReservationFragment())
    }


    private fun navigateToRequestFragment() {
        (activity as? MainActivity)?.navigateToFragment(RequestFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(autoScrollRunnable) // Stop auto-scroll when fragment is destroyed
    }

    private fun fetchAnnouncements() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, "User is not signed in. Please sign in again.", Toast.LENGTH_SHORT).show()
            return
        }

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                originalAnnouncementsList.clear()
                for (dataSnapshot in snapshot.children) {
                    val announcement = dataSnapshot.getValue(Announcement::class.java)
                    if (announcement != null && announcement.title.isNotEmpty()) {
                        originalAnnouncementsList.add(announcement)
                    }
                }

                originalAnnouncementsList.sortByDescending { it.timestamp }
                announcementsAdapter.submitList(originalAnnouncementsList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun filterAnnouncements(query: String) {
        val filteredList = originalAnnouncementsList.filter {
            it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
        }

        announcementsAdapter.submitList(filteredList)
    }

    private fun updateArrowVisibility(position: Int, itemCount: Int) {
        btnLeftArrow.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
        btnRightArrow.visibility = if (position == itemCount - 1) View.INVISIBLE else View.VISIBLE
    }

    private fun addGapBetweenDots(tabLayout: TabLayout) {
        for (i in 0 until tabLayout.tabCount) {
            val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val layoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(16, 0, 16, 0) // Adjust margins to add gap
            tab.requestLayout()
        }
    }
}