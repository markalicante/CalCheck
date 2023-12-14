package com.example.calcheck

data class FoodHistoryItem(
    var date: String? = null,
    var foodItems: List<FoodItem>? = null
) {
    // Add a no-argument constructor
    constructor() : this("", emptyList())
}

