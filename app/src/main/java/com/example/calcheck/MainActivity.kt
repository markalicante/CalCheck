package com.example.calcheck


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    lateinit var dataAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        dataAuth = FirebaseAuth.getInstance()
        val loggedUser = dataAuth.currentUser
        if (loggedUser != null) {
            startActivity(Intent(this, Dashboard::class.java))
        } else
        {
            startActivity(Intent(this, LoginForm::class.java))
        }

        finish()


    }
}