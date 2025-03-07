package com.example.skcamotes

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.skcamotes.RequestFeature.EquipmentsFragment
import com.example.skcamotes.RequestFeature.RequestFragment
import com.example.skcamotes.RequestFeature.SchoolSuppliesFragment
import com.example.skcamotes.RequestFeature.SportsFragment
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity(), RequestFragment.OnFragmentInteractionListener {

    private lateinit var bottomBar: AnimatedBottomBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.purple_700, theme)
        }

        // Initialize bottom bar
        bottomBar = findViewById(R.id.bottom_bar)

        val homeFragment = HomeFragment()
        val requestFragment = RequestFragment()
        val reservationFragment = ReservationFragment()
        val emergenciesFragment = EmergenciesFragment()

        makeCurrentFragment(homeFragment) // Set default fragment

        bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                when (newIndex) {
                    0 -> makeCurrentFragment(homeFragment)
                    1 -> makeCurrentFragment(requestFragment)
                    2 -> makeCurrentFragment(reservationFragment)
                    3 -> makeCurrentFragment(emergenciesFragment)
                    else -> makeCurrentFragment(homeFragment)
                }
                Log.d("bottom_bar", "Selected index: $newIndex, title: ${newTab.title}")
            }

            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {
                Log.d("bottom_bar", "Reselected index: $index, title: ${tab.title}")
            }
        })
    }

    // Implement the method to handle the click event from RequestFragment
    override fun onCardSportsClicked() {
        val sportsFragment = SportsFragment()
        makeCurrentFragment(sportsFragment) // Replace with SportsFragment
    }

    override fun onCardSchoolSuppliesClicked() {
        val schoolSuppliesFragment = SchoolSuppliesFragment()
        makeCurrentFragment(schoolSuppliesFragment) // Replace with SchoolSuppliesFragment
    }

    override fun onCardEquipmentsClicked() {
            val equipmentsFragment = EquipmentsFragment()
        makeCurrentFragment(equipmentsFragment) // Replace with SchoolSuppliesFragment
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    fun openMyReservationsBrgyCalamba() {
        val fragment = MyReservationsBrgyCalamba()
        makeCurrentFragment(fragment)
    }

    fun openMyReservationsThirdFloorFacility() {
        val fragment = MyReservationsThirdFloorFacility()
        makeCurrentFragment(fragment)
    }

    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.wrapper, fragment)
            addToBackStack(null)
            commit()
        }
    }
}
