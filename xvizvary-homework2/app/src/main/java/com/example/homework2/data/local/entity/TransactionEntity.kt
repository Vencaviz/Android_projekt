package com.example.homework2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    /**
     * Stored in minor units (e.g. cents) to avoid floating point errors.
     * Positive number; sign is determined by [type].
     */
    val amountMinor: Long,
    val currencyCode: String,
    val type: TransactionType,
    val category: String,
    /** Epoch millis (UTC) */
    val createdAtMillis: Long,
    /** Optional note/merchant */
    val note: String? = null,
)

enum class TransactionType {
    INCOME,
    EXPENSE,
}
