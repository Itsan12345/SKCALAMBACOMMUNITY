package com.example.skcamotes.Adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skcamotes.AdminSide.AdminLostRequestDataClass
import com.example.skcamotes.R
import com.google.firebase.database.*

class AdminLostFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var returnedRequestsList: MutableList<AdminLostRequestDataClass>
    private lateinit var adapter: AdminLostRequestAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_lost, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewLostEquipmentsRequests)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        returnedRequestsList = mutableListOf()
        adapter = AdminLostRequestAdapter(returnedRequestsList)
        recyclerView.adapter = adapter

        fetchReturnedRequests()

        return view
    }

    private fun fetchReturnedRequests() {
        databaseReference = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("LostRequests")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                returnedRequestsList.clear()
                for (dataSnapshot in snapshot.children) {
                    val request = dataSnapshot.getValue(AdminLostRequestDataClass::class.java)
                    request?.let { returnedRequestsList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
