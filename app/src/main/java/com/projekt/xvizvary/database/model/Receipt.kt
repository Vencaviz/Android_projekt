package com.projekt.xvizvary.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "receipts",
    foreignKeys = [
        ForeignKey(
            entity = Transaction::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["transactionId"])]
)
data class Receipt(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val storeName: String,
    val totalAmount: Double,
    val date: Long, // timestamp in milliseconds
    val rawText: String, // Original OCR text
    val transactionId: Long? = null, // Linked transaction if created
    val createdAt: Long = System.currentTimeMillis()
)
