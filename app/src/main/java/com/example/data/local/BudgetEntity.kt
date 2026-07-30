package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey
    val category: String, // e.g. "Rent", "Groceries", "Electricity Bill"
    val monthlyLimit: Double
)
