package com.example.calcheck

data class FoodItem(
    var itemId: String? = null,  // Unique key for the item
    var name: String? = null,
    var calories: Int? = null
) {
    // Add a no-argument constructor
    constructor() : this("", "", 0)
}
