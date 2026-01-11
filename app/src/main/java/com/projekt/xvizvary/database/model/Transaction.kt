package com.projekt.xvizvary.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["userId"]),
        Index(value = ["firestoreId"], unique = true)
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firestoreId: String = "", // ID from Firestore for sync
    val userId: String = "", // Owner of this transaction
    val name: String,
    val amount: Double,
    val type: TransactionType,
    val categoryId: String? = null, // Changed to String for Firestore compatibility
    val date: Long, // timestamp in milliseconds
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false // Track sync status
)
