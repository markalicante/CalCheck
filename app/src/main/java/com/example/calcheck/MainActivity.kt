package com.example.calcheck

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    //
    lateinit var databaseReference : DatabaseReference
    lateinit var dataAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        databaseReference = FirebaseDatabase.getInstance().getReference("UserAccounts")
        dataAuth = FirebaseAuth.getInstance()

        var editTextUNameLogin : EditText = findViewById(R.id.edtTextUNameLogin)
        var editTextPWLogin : EditText = findViewById(R.id.edtTextPWLogin)
        var toSignup : TextView = findViewById(R.id.signupText)
        var buttonLogin : Button = findViewById(R.id.btnLogin)

        buttonLogin.setOnClickListener(){
            try{
                val username = editTextUNameLogin.text.toString()
                val password = editTextPWLogin.text.toString()

                // Validate if the fields are not empty
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    loginUser(username, password)
                } else {
                    showToast(this, "Please enter username and password.")
                }


            } catch(e : Exception){
                Log.e("Error_CalCheck", e.message.toString())
            }
        }

        toSignup.setOnClickListener {
            startActivity(Intent(this, RegistrationForm::class.java))
        }


    }

    private fun loginUser(email: String, password: String) {
        dataAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showToast(this, "Login successful!")

                    // Get the UID of the authenticated user
                    val userUid = dataAuth.currentUser?.uid

                    // Pass the UID to the function that handles user authentication
                    userUid?.let { uid ->
                        handleUserAuthentication(uid)
                    }
                } else {
                    showToast(this, "Login failed. Please check your credentials.")
                    Log.e("Error_CalCheck", task.exception?.message.toString())
                }
            }
    }

    private fun handleUserAuthentication(uid: String) {
        val userReference = databaseReference.child(uid)

        // Check if the user already exists in the database
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // User exists in the database
                    showToast(applicationContext, "Welcome back!")
                } else {
                    // User doesn't exist in the database
                    showToast(applicationContext, "New user! Welcome!")

                }

                startActivity(Intent(this@MainActivity, Dashboard::class.java))
                finish()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error_CalCheck", "Error checking user existence: ${error.message}")
            }
        })
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}