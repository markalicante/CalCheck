package com.example.calcheck

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodHistoryAdapter : RecyclerView.Adapter<FoodHistoryAdapter.ViewHolder>() {

    private var historyList: List<FoodHistoryItem> = emptyList()

    fun setData(data: List<FoodHistoryItem>) {
        historyList = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyItem = historyList[position]
        holder.bind(historyItem)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.historyNameTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.historyDateTextView)
        private val caloriesTextView: TextView = itemView.findViewById(R.id.historyCaloriesTextView)

        fun bind(historyItem: FoodHistoryItem) {
            // Display the name
            val names = historyItem.foodItems?.map { it.name ?: "Unknown" }?.joinToString(", ") ?: "Unknown"
            nameTextView.text = "Food Names: $names"

            dateTextView.text = "Date: ${historyItem.date}"

            // Calculate total calories from the list of FoodItems
            val totalCalories = historyItem.foodItems?.sumBy { it.calories ?: 0 } ?: 0
            caloriesTextView.text = "Total Calories: $totalCalories"
        }
    }
}
