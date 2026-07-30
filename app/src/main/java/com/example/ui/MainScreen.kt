package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddExpenseBottomSheet
import com.example.ui.components.BankStatementBottomSheet
import com.example.ui.components.SetBudgetDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BudgetsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.theme.IosBlue

@Composable
fun MainScreen(
    viewModel: ExpenseViewModel
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val expenses by viewModel.expensesState.collectAsStateWithLifecycle()
    val filteredExpenses by viewModel.filteredExpenses.collectAsStateWithLifecycle()
    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpenses by viewModel.totalExpenses.collectAsStateWithLifecycle()
    val netBalance by viewModel.netBalance.collectAsStateWithLifecycle()
    val categorySummaries by viewModel.categorySummaries.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val isAddSheetOpen by viewModel.isAddBottomSheetOpen.collectAsStateWithLifecycle()
    val prefilledCategory by viewModel.prefilledCategory.collectAsStateWithLifecycle()
    val prefilledType by viewModel.prefilledType.collectAsStateWithLifecycle()

    val isBudgetModalOpen by viewModel.isBudgetModalOpen.collectAsStateWithLifecycle()
    val targetCategoryForBudget by viewModel.targetCategoryForBudget.collectAsStateWithLifecycle()

    val isStatementSheetOpen by viewModel.isStatementSheetOpen.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    val currentBudgetObj = categorySummaries.find { it.category == targetCategoryForBudget }
    val currentLimit = currentBudgetObj?.budgetLimit ?: 0.0

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddBottomSheet() },
                containerColor = IosBlue,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .testTag("fab_add_expense")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        bottomBar = {
            IosBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.setSelectedTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = selectedTab, label = "main_screen_tabs") { tab ->
                when (tab) {
                    0 -> DashboardScreen(
                        totalIncome = totalIncome,
                        totalExpenses = totalExpenses,
                        netBalance = netBalance,
                        categorySummaries = categorySummaries,
                        recentTransactions = expenses,
                        themeMode = themeMode,
                        onToggleTheme = { viewModel.toggleTheme() },
                        onSetThemeMode = { mode -> viewModel.setThemeMode(mode) },
                        onQuickAdd = { cat, type -> viewModel.openAddBottomSheet(cat, type) },
                        onSetBudget = { cat -> viewModel.openSetBudgetModal(cat) },
                        onOpenStatementAnalyzer = { viewModel.openStatementSheet() },
                        onDeleteTransaction = { exp -> viewModel.deleteTransaction(exp) },
                        onSeeAllTransactions = { viewModel.setSelectedTab(2) }
                    )
                    1 -> AnalyticsScreen(
                        totalExpenses = totalExpenses,
                        totalIncome = totalIncome,
                        categorySummaries = categorySummaries
                    )
                    2 -> TransactionsScreen(
                        expenses = filteredExpenses,
                        selectedFilter = selectedFilter,
                        searchQuery = searchQuery,
                        onFilterSelected = { filter -> viewModel.setSelectedFilter(filter) },
                        onSearchQueryChange = { q -> viewModel.setSearchQuery(q) },
                        onDeleteTransaction = { exp -> viewModel.deleteTransaction(exp) }
                    )
                    3 -> BudgetsScreen(
                        categorySummaries = categorySummaries,
                        onSetBudget = { cat -> viewModel.openSetBudgetModal(cat) },
                        onQuickAdd = { cat -> viewModel.openAddBottomSheet(cat, "EXPENSE") },
                        onClearAllData = { viewModel.clearAllData() }
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet for Adding Transaction
    AddExpenseBottomSheet(
        isOpen = isAddSheetOpen,
        prefilledCategory = prefilledCategory,
        prefilledType = prefilledType,
        onDismiss = { viewModel.closeAddBottomSheet() },
        onSave = { title, amount, type, category, paymentMethod, notes ->
            viewModel.addTransaction(title, amount, type, category, paymentMethod, notes)
        }
    )

    // Modal Dialog for Setting Category Budget Target
    SetBudgetDialog(
        isOpen = isBudgetModalOpen,
        category = targetCategoryForBudget,
        currentLimit = currentLimit,
        onDismiss = { viewModel.closeSetBudgetModal() },
        onSaveBudget = { category, limit ->
            viewModel.setBudgetLimit(category, limit)
        }
    )

    // Modal Bottom Sheet for AI Bank Statement Analysis
    BankStatementBottomSheet(
        isOpen = isStatementSheetOpen,
        onDismiss = { viewModel.closeStatementSheet() },
        onImportTransactions = { parsedList ->
            viewModel.importParsedTransactions(parsedList)
        }
    )
}

@Composable
fun IosBottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .shadow(12.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                testTag = "tab_home"
            )

            BottomNavItem(
                icon = Icons.Default.BarChart,
                label = "Analytics",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                testTag = "tab_analytics"
            )

            BottomNavItem(
                icon = Icons.Default.ListAlt,
                label = "History",
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) },
                testTag = "tab_history"
            )

            BottomNavItem(
                icon = Icons.Default.Savings,
                label = "Budgets",
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) },
                testTag = "tab_budgets"
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val activeColor = IosBlue
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(22.dp)
            )

            if (isSelected) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = activeColor,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}
