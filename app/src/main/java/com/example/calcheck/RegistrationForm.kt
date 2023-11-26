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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistrationForm : AppCompatActivity() {

    lateinit var databaseReference: DatabaseReference
    lateinit var dataAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_form)

        databaseReference = FirebaseDatabase.getInstance().getReference("UserAccounts")
        dataAuth = FirebaseAuth.getInstance()

        var editTextUNameRegister : EditText = findViewById(R.id.edtTextUNameRegister)
        var editTextPWRegister : EditText = findViewById(R.id.edtTextPWRegister)
        var toLogin : TextView = findViewById(R.id.loginText)
        var buttonRegister : Button = findViewById(R.id.btnRegister)

        buttonRegister.setOnClickListener(){
            try{
                val username = editTextUNameRegister.text.toString()
                val password = editTextPWRegister.text.toString()

                // Validate if the fields are not empty
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    registerUser(username, password)
                } else {
                    showToast(this, "Please enter username and password.")
                }

            } catch(e : Exception){
                Log.e("Error_CalCheck", e.message.toString())
            }
        }

        toLogin.setOnClickListener(){
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
    private fun registerUser(email: String, password: String) {
        dataAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showToast(this, "Registration successful!")

                    // Get the UID of the newly registered user
                    val userUid = dataAuth.currentUser?.uid

                    // Pass the UID to the function that handles user registration
                    userUid?.let { uid ->
                        handleUserRegistration(uid)
                    }
                } else {
                    showToast(this, "Registration failed. Please try again.")
                    Log.e("Error_CalCheck", task.exception?.message.toString())
                }
            }
    }

    private fun handleUserRegistration(uid: String) {
        val userReference = databaseReference.child(uid)

        startActivity(Intent(this@RegistrationForm, MainActivity::class.java))
        finish()
    }

    fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}