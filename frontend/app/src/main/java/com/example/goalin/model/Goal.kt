package com.example.goalin.model

data class Goal(
    val id: String,
    val userId: String,
    val name: String,
    val total: Float,
    val amount: Float,
    val notes: String?,
    val category: Category
)
