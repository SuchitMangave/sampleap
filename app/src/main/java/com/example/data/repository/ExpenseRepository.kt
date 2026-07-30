package com.example.data.repository

import com.example.data.local.BudgetEntity
import com.example.data.local.ExpenseDao
import com.example.data.local.ExpenseEntity
import com.example.data.local.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    val allBudgets: Flow<List<BudgetEntity>> = expenseDao.getAllBudgets()

    suspend fun insertExpense(expense: ExpenseEntity): Long {
        return expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun deleteExpenseById(id: Long) {
        expenseDao.deleteExpenseById(id)
    }

    suspend fun clearAllExpenses() {
        expenseDao.clearAllExpenses()
    }

    suspend fun upsertBudget(budget: BudgetEntity) {
        expenseDao.upsertBudget(budget)
    }

    suspend fun deleteBudget(category: String) {
        expenseDao.deleteBudget(category)
    }

    suspend fun seedInitialDataIfEmpty() {
        val currentExpenses = expenseDao.getAllExpenses().first()
        if (currentExpenses.isEmpty()) {
            val calendar = Calendar.getInstance()
            val now = calendar.timeInMillis

            val sampleExpenses = listOf(
                ExpenseEntity(
                    title = "Monthly Salary",
                    amount = 85000.0,
                    type = TransactionType.INCOME.name,
                    category = "Income",
                    paymentMethod = "Net Banking",
                    timestamp = now - (1 * 24 * 60 * 60 * 1000L), // 1 day ago
                    notes = "Monthly tech salary credited"
                ),
                ExpenseEntity(
                    title = "House Rent - 2BHK",
                    amount = 18000.0,
                    type = TransactionType.EXPENSE.name,
                    category = "Rent",
                    paymentMethod = "UPI",
                    timestamp = now - (2 * 24 * 60 * 60 * 1000L),
                    notes = "Landlord transfer via GPay"
                ),
                ExpenseEntity(
                    title = "D-Mart Groceries Supplies",
                    amount = 5400.0,
                    type = TransactionType.EXPENSE.name,
                    category = "Groceries",
                    paymentMethod = "Credit Card",
                    timestamp = now - (3 * 24 * 60 * 60 * 1000L),
                    notes = "Monthly pantry stocking"
                ),
                ExpenseEntity(
                    title = "Electricity Bill (BESCOM)",
                    amount = 2350.0,
                    type = TransactionType.EXPENSE.name,
                    category = "Electricity Bill",
                    paymentMethod = "UPI",
                    timestamp = now - (4 * 24 * 60 * 60 * 1000L),
                    notes = "AC & Appliances consumption"
                ),
                ExpenseEntity(
                    title = "Freelance Project",
                    amount = 12500.0,
                    type = TransactionType.INCOME.name,
                    category = "Income",
                    paymentMethod = "UPI",
                    timestamp = now - (5 * 24 * 60 * 60 * 1000L),
                    notes = "UI Design consultation"
                ),
                ExpenseEntity(
                    title = "Weekend Dinner at Barbeque Nation",
                    amount = 2450.0,
                    type = TransactionType.EXPENSE.name,
                    category = "Food & Dining",
                    paymentMethod = "Credit Card",
                    timestamp = now - (6 * 24 * 60 * 60 * 1000L),
                    notes = "Family dinner"
                ),
                ExpenseEntity(
                    title = "Fuel Petrol - HPCL",
                    amount = 2200.0,
                    type = TransactionType.EXPENSE.name,
                    category = "Transportation",
                    paymentMethod = "UPI",
                    timestamp = now - (7 * 24 * 60 * 60 * 1000L),
                    notes = "Full tank refill"
                ),
                ExpenseEntity(
                    title = "WiFi Fiber Broadband Bill",
                    amount = 999.0,
                    type = TransactionType.EXPENSE.name,
                    category = "Subscriptions",
                    paymentMethod = "UPI",
                    timestamp = now - (8 * 24 * 60 * 60 * 1000L),
                    notes = "300 Mbps unlimited"
                ),
                ExpenseEntity(
                    title = "Fresh Organic Milk & Fruits",
                    amount = 1150.0,
                    type = TransactionType.EXPENSE.name,
                    category = "Groceries",
                    paymentMethod = "UPI",
                    timestamp = now - (9 * 24 * 60 * 60 * 1000L),
                    notes = "Weekly fresh basket"
                )
            )

            val sampleBudgets = listOf(
                BudgetEntity("Rent", 20000.0),
                BudgetEntity("Groceries", 10000.0),
                BudgetEntity("Electricity Bill", 3500.0),
                BudgetEntity("Food & Dining", 8000.0),
                BudgetEntity("Transportation", 5000.0),
                BudgetEntity("Subscriptions", 2500.0)
            )

            expenseDao.insertExpenses(sampleExpenses)
            expenseDao.upsertBudgets(sampleBudgets)
        }
    }
}
