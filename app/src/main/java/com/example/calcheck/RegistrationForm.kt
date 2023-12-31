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
        var validateObject = ValidationClass()

        databaseReference = FirebaseDatabase.getInstance().getReference("UserAccounts")
        dataAuth = FirebaseAuth.getInstance()

        var editTextUNameRegister : EditText = findViewById(R.id.edtTextUNameRegister)
        var editTextEmailRegister : EditText =  findViewById(R.id.edtTextEmailRegister)
        var editTextPWRegister : EditText = findViewById(R.id.edtTextPWRegister)
        var editTextTargetCal : EditText = findViewById(R.id.edtTextTargetCal)
        var toLogin : TextView = findViewById(R.id.loginText)
        var buttonRegister : Button = findViewById(R.id.btnRegister)


        buttonRegister.setOnClickListener(){
            try{
                val username = editTextUNameRegister.text.toString()
                val useremail = editTextEmailRegister.text.toString()
                val password = editTextPWRegister.text.toString()
                val tarcal = editTextTargetCal.text.toString().toInt()

                // Validate if the fields are not empty
                if (username.isNotEmpty() && password.isNotEmpty() && useremail.isNotEmpty()) {
                    if (validateObject.ValidatePassword(password)){
                    registerUser(username, useremail, password, tarcal)
                    }
                    else {
                        showToast(this, "Please enter a valid password.")
                    }
                } else {
                    showToast(this, "Please enter username and password.")
                }

            } catch(e : Exception){
                Log.e("Error_CalCheck", e.message.toString())
            }
        }

        toLogin.setOnClickListener(){
            startActivity(Intent(this, LoginForm::class.java))
        }

    }
    private fun registerUser(username: String, email: String, password: String, targetCal: Int) {
        dataAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showToast(this, "Registration successful!")

                    // Get the UID of the newly registered user
                    val userUid = dataAuth.currentUser?.uid

                    // Pass the UID to the function that handles user registration
                    userUid?.let { uid ->
                        handleUserRegistration(uid, username, email, targetCal)
                    }
                } else {
                    showToast(this, "Registration failed. Please try again.")
                    Log.e("Error_CalCheck", task.exception?.message.toString())
                }
            }
    }

    private fun handleUserRegistration(uid: String, username: String, email: String, targetCal : Int) {
        val userReference = databaseReference.child(uid)

        // Create a UserAccounts object with the necessary data
        val newUser = UserAccounts(username, email, targetCal) // Customize this based on your data model

        // Set the user data in the Realtime Database
        userReference.setValue(newUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast(this@RegistrationForm, "User data saved successfully!")

                    // Redirect to the main activity or another screen
                    startActivity(Intent(this@RegistrationForm, LoginForm::class.java))
                    finish()
                } else {
                    showToast(this@RegistrationForm, "Failed to save user data.")
                    Log.e("Error_CalCheck", task.exception?.message.toString())
                }
            }
    }

    fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}