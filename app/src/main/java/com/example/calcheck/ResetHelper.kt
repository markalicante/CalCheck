import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.calcheck.FoodHistoryItem
import com.example.calcheck.FoodItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResetHelper {

    private lateinit var databaseReference: DatabaseReference

    fun performDailyReset(context: Context) {
        databaseReference = FirebaseDatabase.getInstance().getReference("UserAccounts")
        // Check if it's a new day and reset the list if necessary
        val currentDay = getCurrentDay()
        val lastStoredDay = getLastStoredDayFromPreferences(context)
        Log.d("CalCheck", "Resetting food list for day: $currentDay")

        if (currentDay != lastStoredDay) {
            resetFoodList(context)
            updateLastStoredDayInPreferences(context, currentDay)
        }
    }

    private fun getCurrentDay(): String {
        // Implement a method to get the current day in MM/DD/YYYY format
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    private fun getLastStoredDayFromPreferences(context: Context): String {
        // Retrieve the last stored day from SharedPreferences
        val preferences = context.getSharedPreferences("YOUR_PREFERENCE_NAME", Context.MODE_PRIVATE)
        return preferences.getString("last_day", "") ?: ""
    }

    private fun updateLastStoredDayInPreferences(context: Context, day: String) {
        // Update the last stored day in SharedPreferences
        val preferences = context.getSharedPreferences("YOUR_PREFERENCE_NAME", Context.MODE_PRIVATE)
        Log.d("CalCheck", "Updating last stored day to: $day")
        preferences.edit().putString("last_day", day).apply()
    }

    private fun resetFoodList(context: Context) {
        // Implement the method to reset the food list
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val userFoodRef = databaseReference.child(uid).child("foods")
            userFoodRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val foodItemsForCurrentDay = mutableListOf<FoodItem>()

                    for (snapshot in dataSnapshot.children) {
                        val foodItem = snapshot.getValue(FoodItem::class.java)
                        foodItem?.let {
                            foodItemsForCurrentDay.add(it)
                        }
                    }

                    // Check if there are entries for the current day
                    if (foodItemsForCurrentDay.isNotEmpty()) {
                        val historyList = mutableListOf<FoodHistoryItem>()

                        for (foodItem in foodItemsForCurrentDay) {
                            // Create a FoodHistoryItem and add it to the history list
                            val historyItem = FoodHistoryItem(
                                getCurrentDay(),
                                listOf(FoodItem("Some Food", foodItem.calories ?: 0))
                            )
                            historyList.add(historyItem)
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

                                // Create a FoodHistoryItem and add it to the current history list
                                val newHistoryItem = FoodHistoryItem(
                                    getCurrentDay(),
                                    foodItemsForCurrentDay
                                )
                                currentHistoryList.add(newHistoryItem)

                                // Save the updated history list to the "history" node
                                userHistoryRef.setValue(currentHistoryList)

                                // Remove the data from the "foods" node
                                userFoodRef.removeValue()

                                showToast(context, "Food list reset for today. Data moved to history.")
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Handle errors
                                Log.e("Error_CalCheck", "Error reading user's food history: ${databaseError.message}")
                                showToast(context, "Failed to retrieve food history. Please try again.")
                            }
                        })
                    } else {
                        showToast(context, "No entries for today. Food list not reset.")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    Log.e("Error_CalCheck", "Error resetting user's food list: ${databaseError.message}")
                    showToast(context, "Failed to reset food list. Please try again.")
                }
            })
        }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
