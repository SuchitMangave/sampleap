package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    INCOME,
    EXPENSE
}

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val category: String, // "Income", "Rent", "Groceries", "Electricity Bill", etc.
    val paymentMethod: String = "UPI", // "UPI", "Credit Card", "Debit Card", "Cash", "Net Banking"
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
