package com.example.calcheck


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdFood : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var foodItemId: String

    private lateinit var updateButton: ImageView
    private lateinit var edtTextUpdFood: TextInputEditText
    private lateinit var edtTextUpdCalories: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upd_food)

        // Initialize your DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("UserAccounts")

        // Get the food item ID from the intent
        foodItemId = intent.getStringExtra("foodItemId") ?: ""

        updateButton = findViewById(R.id.updateButton)
        edtTextUpdFood = findViewById(R.id.edtTextUpdFood)
        edtTextUpdCalories = findViewById(R.id.edtTextUpdCalories)

        // Retrieve the food item details and populate the UI
        retrieveFoodItemDetails()

        // Set up the save button click listener
        updateButton.setOnClickListener {
            updateFoodItem()
        }
    }

    private fun retrieveFoodItemDetails() {
        // Retrieve the details of the food item with the given ID
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null && foodItemId.isNotEmpty()) {
            val userFoodRef = databaseReference.child(uid).child("foods").child(foodItemId)
            userFoodRef.get().addOnSuccessListener { snapshot ->
                val foodItem = snapshot.getValue(FoodItem::class.java)
                foodItem?.let {
                    // Populate the UI with the food item details
                    edtTextUpdFood.setText(it.name)
                    edtTextUpdCalories.setText(it.calories.toString())
                }
            }.addOnFailureListener {
                // Handle the failure to retrieve food item details
            }
        }
    }

    private fun updateFoodItem() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null && foodItemId.isNotEmpty()) {
            val userFoodRef = databaseReference.child(uid).child("foods").child(foodItemId)

            // Get the updated values from the UI
            val updatedName = edtTextUpdFood.text.toString()
            val updatedCalories = edtTextUpdCalories.text.toString().toInt()

            // Update the food item in the database
            userFoodRef.child("name").setValue(updatedName)
            userFoodRef.child("calories").setValue(updatedCalories)

            // Show a toast for successful update
            showToast(applicationContext, "Food item updated successfully")

            // Finish the activity after updating
            finish()
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


