package com.example.calcheck

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FoodActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var foodAdapter: FoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)

        Log.d("FoodActivity", "onCreate started")

        // Initialize your DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("UserAccounts")

        foodRecyclerView = findViewById(R.id.recyclerBreakfast)
        foodAdapter = FoodAdapter()

        // Set up your RecyclerView with the adapter
        foodRecyclerView.layoutManager = LinearLayoutManager(this)
        foodRecyclerView.adapter = foodAdapter

        // Assume the user is logged in, so you have the UID
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            // Retrieve the user's food list
            val userFoodRef = databaseReference.child(uid).child("foods")

            userFoodRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val foodList = mutableListOf<FoodItem>()

                    for (snapshot in dataSnapshot.children) {
                        val foodItem = snapshot.getValue(FoodItem::class.java)
                        foodItem?.let {
                            foodList.add(it)
                        }
                    }

                    // Update the adapter with the new data
                    foodAdapter.setData(foodList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    Log.e("Error_CalCheck", "Error reading user's food list: ${databaseError.message}")
                    showToast(applicationContext, "Failed to retrieve the food list. Please try again.")
                }
            })
        }

        val addButton1 = findViewById<ImageView>(R.id.addButton1)
        addButton1.setOnClickListener {
            val intent = Intent(this, AddFood::class.java)
            startActivity(intent)
        }
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun prevPage(view: View) {
        val page2 = Intent(this, Dashboard::class.java)
        startActivity(page2)
    }
}
