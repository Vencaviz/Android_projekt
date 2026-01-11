package com.projekt.xvizvary.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {

    private val czechFormat = NumberFormat.getCurrencyInstance(Locale("cs", "CZ"))

    fun formatCzk(amount: Double): String {
        return czechFormat.format(amount)
    }

    fun formatWithSign(amount: Double, isIncome: Boolean): String {
        val sign = if (isIncome) "+" else "-"
        return "$sign ${formatCzk(kotlin.math.abs(amount))}"
    }

    fun formatCompact(amount: Double): String {
        return when {
            amount >= 1_000_000 -> String.format(Locale.getDefault(), "%.1fM Kč", amount / 1_000_000)
            amount >= 1_000 -> String.format(Locale.getDefault(), "%.1fk Kč", amount / 1_000)
            else -> formatCzk(amount)
        }
    }
}
