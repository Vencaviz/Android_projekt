package com.projekt.xvizvary.firebase.model

import com.google.firebase.firestore.DocumentId

/**
 * Firestore model for Transaction
 */
data class FirestoreTransaction(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val amount: Double = 0.0,
    val type: String = "EXPENSE", // "INCOME" or "EXPENSE"
    val categoryId: String? = null,
    val date: Long = System.currentTimeMillis(),
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-arg constructor for Firestore
    constructor() : this("", "", 0.0, "EXPENSE", null, 0, null, 0)
}
