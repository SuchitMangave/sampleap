package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BudgetEntity
import com.example.data.local.ExpenseEntity
import com.example.data.local.TransactionType
import com.example.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

val CATEGORY_LIST = listOf(
    "Rent",
    "Groceries",
    "Electricity Bill",
    "Food & Dining",
    "Shopping",
    "Transportation",
    "Subscriptions",
    "Health",
    "Entertainment",
    "Income",
    "Other"
)

val PAYMENT_METHODS = listOf(
    "UPI",
    "Credit Card",
    "Debit Card",
    "Net Banking",
    "Cash"
)

data class CategorySummary(
    val category: String,
    val totalSpent: Double,
    val budgetLimit: Double,
    val percentage: Float
)

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpenseRepository

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ExpenseRepository(database.expenseDao())
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    val expensesState: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val budgetsState: StateFlow<List<BudgetEntity>> = repository.allBudgets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI state toggles
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedFilter = MutableStateFlow("ALL") // "ALL", "INCOME", "EXPENSE", or category
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isAddBottomSheetOpen = MutableStateFlow(false)
    val isAddBottomSheetOpen: StateFlow<Boolean> = _isAddBottomSheetOpen.asStateFlow()

    private val _prefilledCategory = MutableStateFlow<String?>(null)
    val prefilledCategory: StateFlow<String?> = _prefilledCategory.asStateFlow()

    private val _prefilledType = MutableStateFlow<String?>(null)
    val prefilledType: StateFlow<String?> = _prefilledType.asStateFlow()

    private val _isBudgetModalOpen = MutableStateFlow(false)
    val isBudgetModalOpen: StateFlow<Boolean> = _isBudgetModalOpen.asStateFlow()

    private val _targetCategoryForBudget = MutableStateFlow("Rent")
    val targetCategoryForBudget: StateFlow<String> = _targetCategoryForBudget.asStateFlow()

    private val _isStatementSheetOpen = MutableStateFlow(false)
    val isStatementSheetOpen: StateFlow<Boolean> = _isStatementSheetOpen.asStateFlow()

    // Filtered Expenses
    val filteredExpenses: StateFlow<List<ExpenseEntity>> = combine(
        expensesState,
        _selectedFilter,
        _searchQuery
    ) { expenses, filter, query ->
        expenses.filter { item ->
            val matchesFilter = when (filter) {
                "ALL" -> true
                "INCOME" -> item.type == TransactionType.INCOME.name
                "EXPENSE" -> item.type == TransactionType.EXPENSE.name
                else -> item.category.equals(filter, ignoreCase = true)
            }
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true) ||
                    item.notes.contains(query, ignoreCase = true) ||
                    item.paymentMethod.contains(query, ignoreCase = true)

            matchesFilter && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Calculated Totals
    val totalIncome: StateFlow<Double> = expensesState
        .combine(_selectedFilter) { list, _ ->
            list.filter { it.type == TransactionType.INCOME.name }.sumOf { it.amount }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpenses: StateFlow<Double> = expensesState
        .combine(_selectedFilter) { list, _ ->
            list.filter { it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netBalance: StateFlow<Double> = combine(totalIncome, totalExpenses) { inc, exp ->
        inc - exp
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Category Summaries with Budgets
    val categorySummaries: StateFlow<List<CategorySummary>> = combine(
        expensesState,
        budgetsState
    ) { expenses, budgets ->
        val expenseItems = expenses.filter { it.type == TransactionType.EXPENSE.name }
        val budgetMap = budgets.associate { it.category to it.monthlyLimit }

        CATEGORY_LIST.filter { it != "Income" }.map { category ->
            val spent = expenseItems.filter { it.category == category }.sumOf { it.amount }
            val limit = budgetMap[category] ?: 0.0
            val pct = if (limit > 0) (spent / limit).toFloat().coerceIn(0f, 1f) else 0f
            CategorySummary(category, spent, limit, pct)
        }.sortedByDescending { it.totalSpent }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun setSelectedFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openAddBottomSheet(category: String? = null, type: String? = null) {
        _prefilledCategory.value = category
        _prefilledType.value = type
        _isAddBottomSheetOpen.value = true
    }

    fun closeAddBottomSheet() {
        _isAddBottomSheetOpen.value = false
        _prefilledCategory.value = null
        _prefilledType.value = null
    }

    fun openSetBudgetModal(category: String) {
        _targetCategoryForBudget.value = category
        _isBudgetModalOpen.value = true
    }

    fun closeSetBudgetModal() {
        _isBudgetModalOpen.value = false
    }

    fun toggleTheme() {
        _themeMode.value = when (_themeMode.value) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun openStatementSheet() {
        _isStatementSheetOpen.value = true
    }

    fun closeStatementSheet() {
        _isStatementSheetOpen.value = false
    }

    fun importParsedTransactions(parsedList: List<com.example.data.api.GeminiStatementAnalyzer.ParsedTransaction>) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            parsedList.forEachIndexed { index, item ->
                val entity = ExpenseEntity(
                    title = item.title,
                    amount = item.amount,
                    type = item.type,
                    category = item.category,
                    paymentMethod = item.paymentMethod,
                    timestamp = now - (index * 100000L),
                    notes = item.notes
                )
                repository.insertExpense(entity)
            }
        }
    }

    fun addTransaction(
        title: String,
        amount: Double,
        type: String,
        category: String,
        paymentMethod: String,
        notes: String
    ) {
        viewModelScope.launch {
            val entity = ExpenseEntity(
                title = title.ifBlank { category },
                amount = amount,
                type = type,
                category = category,
                paymentMethod = paymentMethod,
                timestamp = System.currentTimeMillis(),
                notes = notes
            )
            repository.insertExpense(entity)
            closeAddBottomSheet()
        }
    }

    fun deleteTransaction(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun setBudgetLimit(category: String, limit: Double) {
        viewModelScope.launch {
            repository.upsertBudget(BudgetEntity(category, limit))
            closeSetBudgetModal()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllExpenses()
        }
    }
}
