package com.projekt.xvizvary.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.projekt.xvizvary.database.model.Category
import com.projekt.xvizvary.database.model.Limit
import com.projekt.xvizvary.database.model.Receipt
import com.projekt.xvizvary.database.model.Transaction

@Database(
    entities = [
        Transaction::class,
        Category::class,
        Limit::class,
        Receipt::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun limitDao(): LimitDao
    abstract fun receiptDao(): ReceiptDao
}
