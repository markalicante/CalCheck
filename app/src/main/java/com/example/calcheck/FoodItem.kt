package com.example.calcheck

data class FoodItem(
    var name: String? = null,
    var calories: Int? = null
) {
    // Add a no-argument constructor
    constructor() : this("", 0)
}
