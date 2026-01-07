package com.example.homework2.domain.model

import com.example.homework2.data.local.entity.TransactionType

data class Transaction(
    val id: Long,
    val title: String,
    val money: Money,
    val type: TransactionType,
    val category: String,
    val createdAtMillis: Long,
    val note: String? = null,
)

