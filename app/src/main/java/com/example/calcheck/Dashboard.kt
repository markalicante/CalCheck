package com.example.calcheck

import ResetHelper
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Dashboard : AppCompatActivity() {

    private val resetHelper = ResetHelper()
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
        var nextPage : ImageView = findViewById(R.id.nextPageButton)


        val uid = auth.currentUser?.uid
        val targetCalRef = db.child(uid.toString()).child("userTargetCal")
        val userFoodRef = db.child(uid.toString()).child("foods")

        targetCalRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val targetCalories = dataSnapshot.getValue(Long::class.java)

                if (targetCalories != null) {
                    progressBar.max = targetCalories.toInt()

                    // Retrieve the user's food list
                    userFoodRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(foodDataSnapshot: DataSnapshot) {
                            var totalCalories = 0

                            for (foodSnapshot in foodDataSnapshot.children) {
                                val foodItem = foodSnapshot.getValue(FoodItem::class.java)
                                totalCalories += foodItem?.calories ?: 0
                            }

                            // Update the progress bar and text based on total calories
                            progressBar.progress = totalCalories
                            progressText.text = targetCalories.toInt().toString()
                            currentProgressText.text = totalCalories.toString()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle errors
                        }
                    })

                } else {
                    Log.e("Dashboard", "Target calories value is not a valid integer")
                    showToast(applicationContext, "Invalid target calories value. Please set a valid target.")

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
        resetHelper.performDailyReset(this)
    }



    fun nextPage(view: View){
        val page2 = Intent(this, FoodActivity::class.java)
        startActivity(page2)
    }
    fun toProfile(view: View){
        val profilePage = Intent(this, ProfileActivity::class.java)
        startActivity(profilePage)
    }
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}