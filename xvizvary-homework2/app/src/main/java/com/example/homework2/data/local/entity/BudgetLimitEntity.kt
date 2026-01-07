package com.example.homework2.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "budget_limits",
    indices = [
        Index(value = ["category", "month"], unique = true),
    ],
)
data class BudgetLimitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val category: String,
    /** e.g. "2026-01" */
    val month: String,
    /** Stored in minor units (cents) */
    val limitMinor: Long,
    val currencyCode: String,
)

