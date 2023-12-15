package com.example.calcheck

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FoodActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var foodAdapter: FoodAdapter
    private var TESTING_CURRENT_DAY: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)

        val textViewDate: TextView = findViewById(R.id.dateNow)
        val currentDate = getCurrentDate()
        textViewDate.text = "Current Date: $currentDate"

        Log.d("FoodActivity", "onCreate started")

        // Initialize your DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("UserAccounts")

        foodRecyclerView = findViewById(R.id.recyclerFood)
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

        //val resetButton = findViewById<Button>(R.id.resetButton)
        //resetButton.setOnClickListener {
            //incrementCurrentDay()
        //}

        // Set up the delete click listener in the adapter
        foodAdapter.setOnDeleteClickListener { position ->
            // Handle the delete operation here
            deleteFoodItem(position)
        }

        foodAdapter.setOnItemClickListener { position ->
            val foodItem = foodAdapter.foodList[position]
            val intent = Intent(this, UpdFood::class.java)
            intent.putExtra("foodItemId", foodItem.itemId)
            startActivity(intent)
        }

        val addButton1 = findViewById<ImageView>(R.id.addButton1)
        addButton1.setOnClickListener {
            val intent = Intent(this, AddFood::class.java)
            startActivity(intent)
        }

        val historyButton = findViewById<Button>(R.id.historyButton)
        historyButton.setOnClickListener {
            val intent = Intent(this, FoodHistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun deleteFoodItem(position: Int) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val userFoodRef = databaseReference.child(uid).child("foods")
            val foodItemToDelete = foodAdapter.foodList[position]

            val itemId = foodItemToDelete.itemId
            if (itemId != null) {
                userFoodRef.child(itemId).removeValue()
                showToast(applicationContext, "Food item deleted.")
            } else {
                Log.e("DeleteFoodItem", "Attempted to delete item with null itemId.")
                showToast(applicationContext, "Failed to delete food item.")
            }
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
        val page2 = Intent(this, Dashboard::class.java)
        startActivity(page2)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("current_day", TESTING_CURRENT_DAY)
        super.onSaveInstanceState(outState)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        return currentDate
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        TESTING_CURRENT_DAY = savedInstanceState.getInt("current_day")
    }
}
