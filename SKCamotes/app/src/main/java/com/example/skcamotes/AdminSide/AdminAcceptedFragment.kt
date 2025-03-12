package com.example.skcamotes.AdminSide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skcamotes.Adapters.AdminAcceptedRequestAdapter
import com.example.skcamotes.R
import com.google.firebase.database.*

class AdminAcceptedFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var acceptedRequestsAdapter: AdminAcceptedRequestAdapter
    private lateinit var databaseReference: DatabaseReference
    private var requestList = mutableListOf<AdminAcceptedRequestDataClass>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_accepted, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewAcceptedEquipmentsRequests)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        databaseReference = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("AcceptedRequests")

        fetchAcceptedRequests()

        return view
    }

    private fun fetchAcceptedRequests() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                requestList.clear()
                for (requestSnapshot in snapshot.children) {
                    val request = requestSnapshot.getValue(AdminAcceptedRequestDataClass::class.java)
                    if (request != null) {
                        requestList.add(request)
                    }
                }
                acceptedRequestsAdapter = AdminAcceptedRequestAdapter(requestList)
                recyclerView.adapter = acceptedRequestsAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}
