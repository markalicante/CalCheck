package com.example.calcheck

data class UserAccounts(
    var userName: String = "",
    var userEmail: String = "",
    var userTargetCal: Int = 0
) {
    // No-argument constructor required for Firebase deserialization
    constructor() : this("","", 0)
}