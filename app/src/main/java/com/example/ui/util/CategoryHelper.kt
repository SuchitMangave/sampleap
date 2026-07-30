package com.example.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.ElectricityCategoryColor
import com.example.ui.theme.FoodCategoryColor
import com.example.ui.theme.GroceryCategoryColor
import com.example.ui.theme.HealthCategoryColor
import com.example.ui.theme.IncomeCategoryColor
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosTeal
import com.example.ui.theme.RentCategoryColor
import com.example.ui.theme.ShoppingCategoryColor
import com.example.ui.theme.TransportCategoryColor

object CategoryHelper {

    fun getCategoryIcon(category: String): ImageVector {
        return when (category.lowercase()) {
            "rent", "house rent" -> Icons.Default.Home
            "groceries", "grocery" -> Icons.Default.LocalGroceryStore
            "electricity bill", "electricity", "power bill", "utility" -> Icons.Default.Bolt
            "income", "salary" -> Icons.Default.TrendingUp
            "food & dining", "food", "dining" -> Icons.Default.Restaurant
            "shopping" -> Icons.Default.ShoppingBag
            "transportation", "fuel", "travel" -> Icons.Default.DirectionsCar
            "subscriptions", "bills" -> Icons.Default.Subscriptions
            "health", "medical" -> Icons.Default.MedicalServices
            "entertainment" -> Icons.Default.Movie
            else -> Icons.Default.Payments
        }
    }

    fun getCategoryColor(category: String): Color {
        return when (category.lowercase()) {
            "rent", "house rent" -> RentCategoryColor
            "groceries", "grocery" -> GroceryCategoryColor
            "electricity bill", "electricity", "power bill" -> ElectricityCategoryColor
            "income", "salary" -> IncomeCategoryColor
            "food & dining", "food", "dining" -> FoodCategoryColor
            "shopping" -> ShoppingCategoryColor
            "transportation", "fuel" -> TransportCategoryColor
            "subscriptions" -> IosBlue
            "health", "medical" -> HealthCategoryColor
            "entertainment" -> IosTeal
            else -> IosBlue
        }
    }
}
