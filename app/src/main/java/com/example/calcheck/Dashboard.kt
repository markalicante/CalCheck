package com.example.calcheck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class Dashboard : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()

        var progressBar: ProgressBar = findViewById(R.id.progress_bar)
        var progressText: TextView = findViewById(R.id.progress_text)
        var buttonLogout : Button = findViewById(R.id.btnLogout)



        val progressValue = 50
        progressBar.progress = progressValue
        progressText.text = progressValue.toString()

        buttonLogout.setOnClickListener {
            logoutUser()
        }

    }

    private fun logoutUser() {
        auth.signOut()

        startActivity(Intent(this, MainActivity::class.java))

        finish()
    }

}