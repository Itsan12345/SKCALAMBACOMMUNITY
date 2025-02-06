package com.example.p2_handson_rolloque

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.p2_handson_rolloque.models.User
import com.google.firebase.database.*

class UsersListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: ArrayList<User>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userList = ArrayList()
        userAdapter = UserAdapter(userList)
        recyclerView.adapter = userAdapter

        // Firebase reference to the students node
        databaseReference = FirebaseDatabase.getInstance().getReference("students")

        // Fetch data from Firebase
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()  // Clear the existing list to avoid duplicate entries
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        userList.add(user)  // Add user to the list
                    }
                }
                userAdapter.notifyDataSetChanged()  // Notify the adapter about the data change
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any database errors
            }
        })
    }

    fun deleteUser(userId: String) {
        val userReference = databaseReference.child(userId)
        userReference.removeValue().addOnCompleteListener {
            Toast.makeText(this, "User Deleted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to Delete User", Toast.LENGTH_SHORT).show()
        }
    }

    fun editUser(userId: String) {
        val intent = Intent(this, EditUserActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
}
