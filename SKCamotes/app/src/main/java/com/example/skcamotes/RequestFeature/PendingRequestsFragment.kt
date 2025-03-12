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
    private lateinit var emptyStateLayout: View
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
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PendingRequestsAdapter(itemList)
        recyclerView.adapter = adapter

        val currentUser = auth.currentUser
        currentUser?.let {
            val userEmail = it.email
            val userUid = it.uid

            database = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("UserRequestedEquipments")

            fetchPendingRequests(userEmail, userUid)
        }

        return view
    }

    private fun fetchPendingRequests(userEmail: String?, userUid: String?) {
        if (userEmail == null || userUid == null) return

        database.orderByChild("userEmail").equalTo(userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        processSnapshot(snapshot)
                    } else {
                        fetchByUid(userUid)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("DEBUG", "Error fetching data: ${error.message}")
                }
            })
    }

    private fun fetchByUid(userUid: String) {
        database.orderByChild("userUid").equalTo(userUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    processSnapshot(snapshot)
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
            }
        }

        if (itemList.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
        }

        adapter.notifyDataSetChanged()
    }
}
