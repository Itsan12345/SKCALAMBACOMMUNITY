package com.example.skcamotes

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skcamotes.AdminSide.User
import com.example.skcamotes.databinding.FragmentUsersBinding
import com.google.firebase.database.*
import android.widget.EditText
import androidx.core.widget.addTextChangedListener

class UsersFragment : Fragment() {
    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<User>()
    private val filteredList = mutableListOf<User>() // Store filtered users

    private lateinit var filterEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firebase reference to the "Users" node
        database = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Users")

        // Set up the adapter for the RecyclerView
        userAdapter = UserAdapter(filteredList) // Use filtered list
        binding.userLogin.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }

        // Setup the filter EditText
        filterEditText = binding.filterEditText // Assuming you've added the EditText for filtering in your layout

        // Filter data when text changes
        filterEditText.addTextChangedListener { text ->
            filterUsers(text.toString())
        }

        // Fetch the data from Firebase
        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear() // Clear the list before adding new data
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let {
                        // Check if the user has necessary data (skip if not)
                        if (!it.name.isNullOrEmpty() && !it.email.isNullOrEmpty()) {
                            // Check if the timestamp field exists
                            if (it.timestamp == 0L) {
                                val userId = userSnapshot.key
                                if (userId != null) {
                                    database.child(userId).child("timestamp").setValue(System.currentTimeMillis())
                                }
                            }
                            userList.add(it)
                        }
                    }
                }
                filteredList.clear()  // Clear the filtered list and update
                filteredList.addAll(userList) // Add all users to filtered list initially
                userAdapter.notifyDataSetChanged() // Notify the adapter that data has changed
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if the database fetch fails
            }
        })
    }

    private fun filterUsers(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(userList) // If no query, show all users
        } else {
            val lowerCaseQuery = query.lowercase()
            for (user in userList) {
                if (user.name?.lowercase()?.contains(lowerCaseQuery) == true ||
                    user.email?.lowercase()?.contains(lowerCaseQuery) == true ||
                    user.role?.lowercase()?.contains(lowerCaseQuery) == true) {
                    filteredList.add(user)
                }
            }
        }
        userAdapter.notifyDataSetChanged() // Notify adapter about the change
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
