package com.projekt.xvizvary.database.model

/**
 * Data class representing a limit with its current spending amount.
 * Used for displaying limit progress in the UI.
 */
data class LimitWithSpent(
    val limit: Limit,
    val category: Category,
    val spentAmount: Double
) {
    val remainingAmount: Double
        get() = limit.limitAmount - spentAmount

    val progress: Float
        get() = if (limit.limitAmount > 0) {
            (spentAmount / limit.limitAmount).toFloat().coerceIn(0f, 1f)
        } else 0f

    val isOverBudget: Boolean
        get() = spentAmount > limit.limitAmount
}
