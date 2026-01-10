package com.projekt.xvizvary.firebase.model

import com.google.firebase.firestore.DocumentId

/**
 * Firestore model for Limit
 */
data class FirestoreLimit(
    @DocumentId
    val id: String = "",
    val categoryId: String = "",
    val limitAmount: Double = 0.0,
    val periodMonths: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-arg constructor for Firestore
    constructor() : this("", "", 0.0, 1, 0)
}
