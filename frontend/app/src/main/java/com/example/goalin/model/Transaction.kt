package com.example.goalin.model

data class Transaction (
    val id: String,
    val goalId: String,
    val amount: Float,
    val wallet: Wallet,
    val createdAt: String
)