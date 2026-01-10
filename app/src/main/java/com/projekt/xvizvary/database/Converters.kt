package com.projekt.xvizvary.database

import androidx.room.TypeConverter
import com.projekt.xvizvary.database.model.TransactionType

class Converters {

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
}
