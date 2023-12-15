package com.example.calcheck

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddFood : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        // Initialize your DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("UserAccounts")

        val addFoodButton: ImageView = findViewById(R.id.addFoodButton)

        addFoodButton.setOnClickListener {
            addFood()
        }
    }

    private fun addFood() {
        val foodNameEditText: EditText = findViewById(R.id.edtTextFood)
        val caloriesEditText: EditText = findViewById(R.id.edtTextCalories)

        val foodName = foodNameEditText.text.toString()
        val calories = caloriesEditText.text.toString().toInt()

        // Assume the user is logged in, so you have the UID
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null && foodName.isNotEmpty()) {
            val userFoodRef = databaseReference.child(uid).child("foods")

            // Generate a unique key for the new food item
            val newItemKey = userFoodRef.push().key

            if (newItemKey != null) {
                // Create a new FoodItem with the generated key
                val foodItem = FoodItem(newItemKey, foodName, calories)

                // Append the new food item to the user's food list
                userFoodRef.child(newItemKey).setValue(foodItem)

                // Clear input fields after adding
                foodNameEditText.text.clear()
                caloriesEditText.text.clear()

                showToast(this, "Food added successfully!")
            } else {
                showToast(this, "Failed to generate a unique key for the new food item.")
            }
        } else {
            showToast(this, "Invalid input. Please check your entries.")
        }
    }

    fun prevPage(view: View) {
        val page2 = Intent(this, FoodActivity::class.java)
        startActivity(page2)
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
