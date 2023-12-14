package com.example.calcheck

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FoodHistoryActivity : AppCompatActivity() {

    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: FoodHistoryAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_history)

        // Initialize your DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("UserAccounts")

        historyRecyclerView = findViewById(R.id.recyclerHistory)
        historyAdapter = FoodHistoryAdapter()

        // Set up your RecyclerView with the adapter
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        // Assume the user is logged in, so you have the UID
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            // Retrieve the user's historical food data
            val userHistoryRef = databaseReference.child(uid).child("history")

            userHistoryRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val historyList = mutableListOf<FoodHistoryItem>()

                    for (snapshot in dataSnapshot.children) {
                        val historyItem = snapshot.getValue(FoodHistoryItem::class.java)
                        historyItem?.let {
                            historyList.add(it)
                        }
                    }

                    // Update the adapter with the historical data
                    historyAdapter.setData(historyList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    Log.e("Error_CalCheck", "Error reading user's food history: ${databaseError.message}")
                    showToast(applicationContext, "Failed to retrieve food history. Please try again.")
                }
            })
        }
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun toProfile(view: View){
        val profilePage = Intent(this, ProfileActivity::class.java)
        startActivity(profilePage)
    }

    fun prevPage(view: View) {
        val page2 = Intent(this, FoodActivity::class.java)
        startActivity(page2)
    }
}