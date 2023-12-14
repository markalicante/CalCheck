package com.example.calcheck

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.android.material.textfield.TextInputEditText

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("UserAccounts")

        // Reference to the TextInputEditText views
        val usernameEditText: TextInputEditText = findViewById(R.id.edtTextUName)
        val emailEditText: TextInputEditText = findViewById(R.id.edtTextEmail)
        val passwordEditText: TextInputEditText = findViewById(R.id.edtTextPassword)
        val targetCaloriesEditText: TextInputEditText = findViewById(R.id.edtTextTargetCalories)

        // Reference to the Update button
        val updateButton: Button = findViewById(R.id.btnUpdate)
        val logoutButton: Button = findViewById(R.id.btnLogout)

        // Disable editing for username and email
        usernameEditText.isEnabled = false
        emailEditText.isEnabled = false

        // Get the currently signed-in user
        val currentUser: FirebaseUser? = auth.currentUser

        // Retrieve user data from the database
        currentUser?.uid?.let { uid ->
            databaseReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(UserAccounts::class.java)
                        if (user != null) {
                            // Set the fetched data to the views
                            usernameEditText.setText(user.userName)
                            emailEditText.setText(user.userEmail)
                            passwordEditText.setText("********") // Display asterisks for the password for security
                            targetCaloriesEditText.setText(user.userTargetCal.toString())
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }

// Update button click listener
        updateButton.setOnClickListener {
            val newPassword = passwordEditText.text.toString()
            val newTargetCalories = targetCaloriesEditText.text.toString().toInt()

            // Update password in Firebase Authentication
            currentUser?.updatePassword(newPassword)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast(this, "Password updated successfully!")
                    } else {
                        showToast(this, "Failed to update password.")
                        Log.e("Error_CalCheck", task.exception?.message.toString())
                    }
                }

            // Update target calories in Realtime Database
            currentUser?.uid?.let { uid ->
                val userReference = databaseReference.child(uid)
                userReference.child("userTargetCal").setValue(newTargetCalories)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast(this, "Target calories updated successfully!")
                        } else {
                            showToast(this, "Failed to update target calories.")
                            Log.e("Error_CalCheck", task.exception?.message.toString())
                        }
                    }
            }
        }

        logoutButton.setOnClickListener(){
            logoutUser()
        }
    }
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun prevPage(view: View){
        val page2 = Intent(this, Dashboard::class.java)
        startActivity(page2)
    }

    private fun logoutUser() {
        auth.signOut()


        startActivity(Intent(this, LoginForm::class.java))

        finish()
    }
}
