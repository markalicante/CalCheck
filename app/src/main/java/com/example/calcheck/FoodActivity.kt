package com.example.calcheck

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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
import java.util.Calendar

class FoodActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var foodAdapter: FoodAdapter
    private var TESTING_CURRENT_DAY: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)



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

        val resetButton = findViewById<Button>(R.id.resetButton)
        resetButton.setOnClickListener {
            incrementCurrentDay()
            performDailyReset()
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

    private fun performDailyReset() {
        // Check if it's a new day and reset the list if necessary
        val currentDay = getCurrentDay()
        val lastStoredDay = getLastStoredDayFromPreferences()
        Log.d("CalCheck", "Resetting food list for day: $currentDay")

        if (currentDay != lastStoredDay) {
            resetFoodList()
            updateLastStoredDayInPreferences(currentDay)
        }
    }

    private fun incrementCurrentDay() {
        TESTING_CURRENT_DAY++
        Log.d("CalCheck", "Current day incremented to: $TESTING_CURRENT_DAY")
    }
    private fun getCurrentDay(): Int {
        // Implement a method to get the current day (e.g., using Calendar)
        //val calendar = Calendar.getInstance()
        //return calendar.get(Calendar.DAY_OF_YEAR)

        return TESTING_CURRENT_DAY
    }


    private fun getLastStoredDayFromPreferences(): Int {
        // Retrieve the last stored day from SharedPreferences
        val preferences = getPreferences(Context.MODE_PRIVATE)
        return preferences.getInt("last_day", -1)
    }

    private fun updateLastStoredDayInPreferences(day: Int) {
        // Update the last stored day in SharedPreferences
        val preferences = getPreferences(Context.MODE_PRIVATE)
        Log.d("CalCheck", "Updating last stored day to: $day")
        preferences.edit().putInt("last_day", day).apply()
    }

    private fun resetFoodList() {
        // Implement the method to reset the food list
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val userFoodRef = databaseReference.child(uid).child("foods")
            userFoodRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val historyList = mutableListOf<FoodHistoryItem>()

                    for (snapshot in dataSnapshot.children) {
                        val foodItem = snapshot.getValue(FoodItem::class.java)
                        foodItem?.let {
                            // Create a FoodHistoryItem and add it to the history list
                            val historyItem = FoodHistoryItem(getCurrentDay().toString(), listOf(FoodItem("Some Food", it.calories ?: 0)))
                            historyList.add(historyItem)
                        }
                    }

                    // Fetch the current history
                    val userHistoryRef = databaseReference.child(uid).child("history")
                    userHistoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(historyDataSnapshot: DataSnapshot) {
                            val currentHistoryList = mutableListOf<FoodHistoryItem>()

                            for (historySnapshot in historyDataSnapshot.children) {
                                val historyItem = historySnapshot.getValue(FoodHistoryItem::class.java)
                                historyItem?.let {
                                    currentHistoryList.add(it)
                                }
                            }

                            // Create a list of FoodItem objects for the current day
                            val foodItemsForCurrentDay = mutableListOf<FoodItem>()

                            for (snapshot in dataSnapshot.children) {
                                val foodItem = snapshot.getValue(FoodItem::class.java)
                                foodItem?.let {
                                    foodItemsForCurrentDay.add(it)
                                }
                            }

                            // Create a FoodHistoryItem and add it to the current history list
                            val newHistoryItem = FoodHistoryItem(getCurrentDay().toString(), foodItemsForCurrentDay)
                            currentHistoryList.add(newHistoryItem)

                            // Save the updated history list to the "history" node
                            userHistoryRef.setValue(currentHistoryList)

                            // Remove the data from the "foods" node
                            userFoodRef.removeValue()

                            showToast(applicationContext, "Food list reset for today. Data moved to history.")
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle errors
                            Log.e("Error_CalCheck", "Error reading user's food history: ${databaseError.message}")
                            showToast(applicationContext, "Failed to retrieve food history. Please try again.")
                        }
                    })
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    Log.e("Error_CalCheck", "Error resetting user's food list: ${databaseError.message}")
                    showToast(applicationContext, "Failed to reset food list. Please try again.")
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
        val page2 = Intent(this, Dashboard::class.java)
        startActivity(page2)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("current_day", TESTING_CURRENT_DAY)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        TESTING_CURRENT_DAY = savedInstanceState.getInt("current_day")
    }
}
