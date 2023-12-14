package com.example.calcheck

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        // Assume user is logged in, so you have the UID
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null && foodName.isNotEmpty()) {
            val foodItem = FoodItem(foodName, calories)

            // Append the new food item to the user's food list
            val userFoodRef = databaseReference.child(uid).child("foods")
            userFoodRef.push().setValue(foodItem)

            // Clear input fields after adding
            foodNameEditText.text.clear()
            caloriesEditText.text.clear()

            showToast(this, "Food added successfully!")
        } else {
            showToast(this, "Invalid input. Please check your entries.")
        }
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
