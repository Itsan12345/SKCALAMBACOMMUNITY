package com.example.skcamotes.AdminSide

import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.skcamotes.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.skcamotes.R

class AdminPage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var databaseRequests: DatabaseReference
    private lateinit var databaseReservations: DatabaseReference
    private lateinit var navigationView: NavigationView
    private lateinit var menu: Menu

    private var requestCount = 0
    private var reservationCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_page)

        // Initialize FirebaseAuth and GoogleSignInClient
        auth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)

        // Initialize Firebase Database
        databaseRequests = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("UserRequestedEquipments")

        databaseReservations = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("reservationreceipt")

        // Toolbar setup
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        // Drawer layout & navigation view
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Access navigation menu
        menu = navigationView.menu

        // Fetch Badge Counts
        fetchRequestsCount()
        fetchReservationsCount()

        // Load Default Fragment (Users)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UsersFragment())
                .commit()
            navigationView.setCheckedItem(R.id.nav_users)
        }
    }

    // Fetch Requests Count from Firebase
    private fun fetchRequestsCount() {
        databaseRequests.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                requestCount = snapshot.childrenCount.toInt()
                showBadge(R.id.nav_requests, requestCount)
                updateMenuIconBadge()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    // Fetch Reservations Count from Firebase
    private fun fetchReservationsCount() {
        databaseReservations.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reservationCount = snapshot.childrenCount.toInt()
                showBadge(R.id.nav_reservations, reservationCount)
                updateMenuIconBadge()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    // Show Badge for Requests and Reservations in Side Menu
    private fun showBadge(menuItemId: Int, count: Int) {
        val menuItem = menu.findItem(menuItemId)
        val actionView = menuItem.actionView
        val badgeTextView = actionView?.findViewById<View>(R.id.badge_count) as? TextView

        if (count > 0) {
            badgeTextView?.visibility = View.VISIBLE
            badgeTextView?.text = count.toString()
        } else {
            badgeTextView?.visibility = View.GONE
        }
    }

    // Update Toolbar Icon (ic_menu) with Notification Badge
    private fun updateMenuIconBadge() {
        val shouldShowBadge = (requestCount > 0 || reservationCount > 0)

        if (shouldShowBadge) {
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu) // Replace with your custom ic_menu with badge
        } else {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_users -> switchFragment(UsersFragment())
            R.id.nav_announcements -> switchFragment(AnnouncementsFragment())
            R.id.nav_requests -> switchFragment(AdminRequestsFragment())
            R.id.nav_requests_history -> switchFragment(AdminRequestedFragment())
            R.id.nav_reservations -> switchFragment(AdminReservationsFragment())
            R.id.nav_logout -> showSignOutDialog()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun switchFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun showSignOutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { _, _ ->
                googleSignInClient.signOut().addOnCompleteListener {
                    auth.signOut()
                    val intent = Intent(this, LoginPage::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}
