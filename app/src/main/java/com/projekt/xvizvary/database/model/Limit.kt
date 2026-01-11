package com.projekt.xvizvary.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "limits",
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["userId"]),
        Index(value = ["firestoreId"], unique = true)
    ]
)
data class Limit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firestoreId: String = "", // ID from Firestore for sync
    val userId: String = "", // Owner of this limit
    val categoryId: String, // Changed to String for Firestore compatibility
    val limitAmount: Double,
    val periodMonths: Int = 1, // 1 = monthly, 3 = quarterly, 12 = yearly
    val createdAt: Long = System.currentTimeMillis()
)
