package com.example.skcamotes.RequestFeature

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skcamotes.Adapters.PendingRequestsAdapter
import com.example.skcamotes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PendingRequestsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PendingRequestsAdapter
    private lateinit var database: DatabaseReference
    private val itemList = mutableListOf<UserRequestsDataClass>()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pending_requests, container, false)

        recyclerView = view.findViewById(R.id.recyclerView_pending_requests)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PendingRequestsAdapter(itemList)
        recyclerView.adapter = adapter

        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userEmail = currentUser.email
            val userUid = currentUser.uid

            Log.d("DEBUG", "Logged in as Google user: $userEmail, UID: $userUid")

            database = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("UserRequestedEquipments")

            fetchPendingRequests(userEmail, userUid)
        } else {
            Log.e("DEBUG", "No authenticated user")
        }

        return view
    }

    private fun fetchPendingRequests(userEmail: String?, userUid: String?) {
        if (userEmail == null || userUid == null) {
            Log.e("DEBUG", "User email or UID is null")
            return
        }

        database.orderByChild("userEmail").equalTo(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d("DEBUG", "Data fetched for email: $userEmail")
                    processSnapshot(snapshot)
                } else {
                    Log.d("DEBUG", "No data found for email: $userEmail. Trying UID.")
                    fetchByUid(userUid)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DEBUG", "Error fetching data: ${error.message}")
            }
        })
    }

    private fun fetchByUid(userUid: String) {
        database.orderByChild("userUid").equalTo(userUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d("DEBUG", "Data fetched for UID: $userUid")
                    processSnapshot(snapshot)
                } else {
                    Log.e("DEBUG", "No data found for UID: $userUid")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DEBUG", "Error fetching data: ${error.message}")
            }
        })
    }

    private fun processSnapshot(snapshot: DataSnapshot) {
        itemList.clear()
        for (data in snapshot.children) {
            val item = data.getValue(UserRequestsDataClass::class.java)
            if (item != null) {
                itemList.add(item)
                Log.d("DEBUG", "Item added: $item")
            }
        }
        if (itemList.isEmpty()) {
            Log.d("DEBUG", "No items found for the user.")
        }
        adapter.notifyDataSetChanged()
    }
}