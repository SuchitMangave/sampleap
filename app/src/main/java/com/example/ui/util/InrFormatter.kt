package com.example.ui.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InrFormatter {

    fun formatInr(amount: Double, showSymbol: Boolean = true): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val formatted = format.format(amount)
        return if (showSymbol) {
            formatted
        } else {
            formatted.replace("₹", "").trim()
        }
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
