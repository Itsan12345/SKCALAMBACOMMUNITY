package com.example.firebaserealtimedatabaseapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

data class Student(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val gender: String = "",
    val course: String = "",
    val yearLevel: String = ""
)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Input fields
        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etGender = findViewById<EditText>(R.id.etGender)
        val etCourse = findViewById<EditText>(R.id.etCourse)
        val etYearLevel = findViewById<EditText>(R.id.etYearLevel)

        // Submit button
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        // Firebase Realtime Database reference
        val database = FirebaseDatabase.getInstance("https://fir-realtime-database-ap-1b221-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val studentsRef = database.getReference("students")

        btnSubmit.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val gender = etGender.text.toString().trim()
            val course = etCourse.text.toString().trim()
            val yearLevel = etYearLevel.text.toString().trim()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && gender.isNotEmpty() && course.isNotEmpty() && yearLevel.isNotEmpty()) {
                val id = studentsRef.push().key ?: ""
                val student = Student(id, firstName, lastName, gender, course, yearLevel)

                studentsRef.child(id).setValue(student)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Data successfully pushed to Firebase!", Toast.LENGTH_SHORT).show()
                            etFirstName.text.clear()
                            etLastName.text.clear()
                            etGender.text.clear()
                            etCourse.text.clear()
                            etYearLevel.text.clear()
                        } else {
                            Toast.makeText(this, "Failed to push data: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
