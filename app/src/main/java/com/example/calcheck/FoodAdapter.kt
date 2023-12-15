package com.example.calcheck

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    var foodList: List<FoodItem> = emptyList()

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

        // Set up the click listener for updating the food item
        holder.btnUpdate.setOnClickListener {
            onItemClickListener?.invoke(position)
        }

        // Set up the delete click listener
        holder.btnDelete.setOnClickListener {
            onDeleteClickListener?.invoke(position)
        }
    }

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnUpdate : Button = itemView.findViewById(R.id.btnUpd)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(foodItem: FoodItem) {
            // Bind your data to the item view
            val foodNameTextView: TextView = itemView.findViewById(R.id.foodNameTextView)
            val caloriesTextView: TextView = itemView.findViewById(R.id.caloriesTextView)

            foodNameTextView.text = foodItem.name
            caloriesTextView.text = "Calories: " + foodItem.calories.toString()
        }
    }

    private var onDeleteClickListener: ((Int) -> Unit)? = null

    fun setOnDeleteClickListener(listener: (Int) -> Unit) {
        onDeleteClickListener = listener
    }
}

