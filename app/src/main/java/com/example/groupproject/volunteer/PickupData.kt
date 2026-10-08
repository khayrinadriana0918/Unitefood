package com.example.groupproject.volunteer

data class PickupData(
    val foodName: String,
    val category: String,
    val quantity: String,
    val area: String,
    val date: String,
    val time: String,
    var status: String
)