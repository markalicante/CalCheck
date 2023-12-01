package com.example.calcheck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Dashboard : AppCompatActivity() {

    lateinit var db : DatabaseReference
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        db = FirebaseDatabase.getInstance().getReference("UserAccounts/")
        auth = FirebaseAuth.getInstance()

        var progressBar: ProgressBar = findViewById(R.id.progress_bar)
        var progressText: TextView = findViewById(R.id.progress_text)
        var currentProgressText : TextView = findViewById(R.id.current_progress_text)
        var buttonLogout : Button = findViewById(R.id.btnLogout)

        currentProgressText.text = "500"



        val uid = auth.currentUser?.uid
        val valueRef = db.child(uid.toString()).child("userTargetCal")

        valueRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val progressValue = dataSnapshot.getValue(Long::class.java)

                if (progressValue != null) {
                    progressBar.max = progressValue.toInt()
                    progressBar.progress = currentProgressText.text.toString().toInt()
                    progressText.text = progressValue.toString()
                } else {
                    // Handle the case where the value is not a valid integer
                    // (e.g., log an error, set a default value, etc.)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })

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