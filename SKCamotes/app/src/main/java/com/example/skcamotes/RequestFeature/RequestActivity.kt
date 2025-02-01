package com.example.skcamotes.RequestFeature

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.skcamotes.R
import com.example.skcamotes.Adapters.RequestPagerAdapter

class RequestActivity : AppCompatActivity() {

    private lateinit var pendingTab: TextView
    private lateinit var acceptedTab: TextView
    private lateinit var rejectedTab: TextView
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        pendingTab = findViewById(R.id.tab_pending)
        acceptedTab = findViewById(R.id.tab_accepted)
        rejectedTab = findViewById(R.id.tab_rejected)
        viewPager = findViewById(R.id.view_pager)

        val adapter = RequestPagerAdapter(supportFragmentManager)
        adapter.addFragment(PendingRequestsFragment(), "Pending")
        adapter.addFragment(AcceptedRequestsFragment(), "Accepted")
        adapter.addFragment(RejectedRequestsFragment(), "Rejected")

        viewPager.adapter = adapter

        setupTabListeners()
        updateTabUI(0) // Set the default selected tab to "Pending"
    }

    private fun setupTabListeners() {
        pendingTab.setOnClickListener {
            viewPager.currentItem = 0
            updateTabUI(0)
        }

        acceptedTab.setOnClickListener {
            viewPager.currentItem = 1
            updateTabUI(1)
        }

        rejectedTab.setOnClickListener {
            viewPager.currentItem = 2
            updateTabUI(2)
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
            0 -> {
                pendingTab.setBackgroundResource(R.drawable.selected_tab_background)
                pendingTab.setTextColor(getColor(R.color.white))
            }
            1 -> {
                acceptedTab.setBackgroundResource(R.drawable.selected_tab_background)
                acceptedTab.setTextColor(getColor(R.color.white))
            }
            2 -> {
                rejectedTab.setBackgroundResource(R.drawable.selected_tab_background)
                rejectedTab.setTextColor(getColor(R.color.white))
            }
        }
    }

    private fun resetTabStyles() {
        val defaultTextColor = getColor(R.color.white)
        pendingTab.setBackgroundResource(0)
        acceptedTab.setBackgroundResource(0)
        rejectedTab.setBackgroundResource(0)

        pendingTab.setTextColor(defaultTextColor)
        acceptedTab.setTextColor(defaultTextColor)
        rejectedTab.setTextColor(defaultTextColor)
    }
}
