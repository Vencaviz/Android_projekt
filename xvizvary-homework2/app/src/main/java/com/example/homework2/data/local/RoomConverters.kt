package com.example.homework2.data.local

import androidx.room.TypeConverter
import com.example.homework2.data.local.entity.TransactionType

class RoomConverters {
    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? =
        value?.let { TransactionType.valueOf(it) }

    @TypeConverter
    fun fromTransactionType(value: TransactionType?): String? = value?.name
}

