package com.example.skcamotes.RequestFeature

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.skcamotes.R
import com.example.skcamotes.Adapters.RequestPagerAdapter

class RequestActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var pendingTab: TextView
    private lateinit var acceptedTab: TextView
    private lateinit var rejectedTab: TextView
    private lateinit var tabIndicator: View
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        backButton = findViewById(R.id.back_button)
        pendingTab = findViewById(R.id.tab_pending)
        acceptedTab = findViewById(R.id.tab_accepted)
        rejectedTab = findViewById(R.id.tab_rejected)
        tabIndicator = findViewById(R.id.tab_indicator)
        viewPager = findViewById(R.id.view_pager)

        val adapter = RequestPagerAdapter(supportFragmentManager)
        adapter.addFragment(PendingRequestsFragment(), "Pending")
        adapter.addFragment(AcceptedRequestsFragment(), "Accepted")
        adapter.addFragment(RejectedRequestsFragment(), "Rejected")

        viewPager.adapter = adapter

        setupTabListeners()
        updateTabUI(0) // Default to Pending Tab

        // Back button functionality
        backButton.setOnClickListener {
            finish() // Closes the activity
        }
    }

    private fun setupTabListeners() {
        pendingTab.setOnClickListener {
            viewPager.currentItem = 0
        }

        acceptedTab.setOnClickListener {
            viewPager.currentItem = 1
        }

        rejectedTab.setOnClickListener {
            viewPager.currentItem = 2
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                updateTabUI(position)
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun updateTabUI(selectedTab: Int) {
        resetTabStyles()

        when (selectedTab) {
            0 -> highlightTab(pendingTab)
            1 -> highlightTab(acceptedTab)
            2 -> highlightTab(rejectedTab)
        }

        animateIndicator(selectedTab)
    }

    private fun resetTabStyles() {
        val defaultTextColor = getColor(R.color.white)

        pendingTab.setTextColor(defaultTextColor)
        acceptedTab.setTextColor(defaultTextColor)
        rejectedTab.setTextColor(defaultTextColor)
    }

    private fun highlightTab(selectedTab: TextView) {
        selectedTab.setTextColor(getColor(R.color.white))
    }

    private fun animateIndicator(position: Int) {
        val indicatorTranslation = when (position) {
            0 -> pendingTab.x
            1 -> acceptedTab.x
            2 -> rejectedTab.x
            else -> pendingTab.x
        }

        tabIndicator.animate().x(indicatorTranslation).setDuration(200).start()
    }
}
