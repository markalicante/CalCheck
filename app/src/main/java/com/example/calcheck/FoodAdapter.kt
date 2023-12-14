package com.example.calcheck

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    private var foodList: List<FoodItem> = emptyList()

    fun setData(data: List<FoodItem>) {
        foodList = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val foodItem = foodList[position]
        holder.bind(foodItem)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(foodItem: FoodItem) {
            // Bind your data to the item view
            val foodNameTextView: TextView = itemView.findViewById(R.id.foodNameTextView)
            val caloriesTextView: TextView = itemView.findViewById(R.id.caloriesTextView)

            foodNameTextView.text = foodItem.name
            caloriesTextView.text = "Calories: " + foodItem.calories.toString()
        }
    }
}
