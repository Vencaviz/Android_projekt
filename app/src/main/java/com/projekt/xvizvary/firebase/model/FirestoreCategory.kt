package com.projekt.xvizvary.firebase.model

import com.google.firebase.firestore.DocumentId

/**
 * Firestore model for Category
 */
data class FirestoreCategory(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val icon: String = "",
    val color: Long = 0,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-arg constructor for Firestore
    constructor() : this("", "", "", 0, false, 0)

    companion object {
        /**
         * Default categories to create for new users
         */
        fun getDefaultCategories(): List<FirestoreCategory> = listOf(
            FirestoreCategory(
                id = "food",
                name = "Food",
                icon = "restaurant",
                color = 0xFFE57373,
                isDefault = true
            ),
            FirestoreCategory(
                id = "transport",
                name = "Transport",
                icon = "directions_car",
                color = 0xFF64B5F6,
                isDefault = true
            ),
            FirestoreCategory(
                id = "shopping",
                name = "Shopping",
                icon = "shopping_bag",
                color = 0xFFBA68C8,
                isDefault = true
            ),
            FirestoreCategory(
                id = "entertainment",
                name = "Entertainment",
                icon = "movie",
                color = 0xFFFFB74D,
                isDefault = true
            ),
            FirestoreCategory(
                id = "bills",
                name = "Bills",
                icon = "receipt",
                color = 0xFF4DB6AC,
                isDefault = true
            ),
            FirestoreCategory(
                id = "health",
                name = "Health",
                icon = "medical_services",
                color = 0xFFEF5350,
                isDefault = true
            ),
            FirestoreCategory(
                id = "salary",
                name = "Salary",
                icon = "payments",
                color = 0xFF81C784,
                isDefault = true
            ),
            FirestoreCategory(
                id = "other",
                name = "Other",
                icon = "more_horiz",
                color = 0xFF90A4AE,
                isDefault = true
            )
        )
    }
}
