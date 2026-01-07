package com.example.homework2.domain.model

data class BudgetLimit(
    val id: Long,
    val category: String,
    /** e.g. "2026-01" */
    val month: String,
    val limit: Money,
)

