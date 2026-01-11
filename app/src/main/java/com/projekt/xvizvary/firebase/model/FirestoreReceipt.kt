package com.projekt.xvizvary.firebase.model

import com.google.firebase.firestore.DocumentId

/**
 * Firestore model for Receipt
 */
data class FirestoreReceipt(
    @DocumentId
    val id: String = "",
    val storeName: String = "",
    val totalAmount: Double = 0.0,
    val date: Long = System.currentTimeMillis(),
    val rawText: String = "",
    val transactionId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-arg constructor for Firestore
    constructor() : this("", "", 0.0, 0, "", null, 0)
}
