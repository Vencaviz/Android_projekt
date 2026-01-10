package com.projekt.xvizvary.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


import com.projekt.xvizvary.database.model.Receipt


@Database(entities = [Receipt::class], version = 1, exportSchema = true)
abstract class ReceiptDatabase : RoomDatabase(){

    abstract fun receiptDao() : ReceiptDao

    companion object {
        private var INSTANCE: ReceiptDatabase? = null

        fun getDatabase(context: Context): ReceiptDatabase {
            if (INSTANCE == null) {
                synchronized(ReceiptDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            ReceiptDatabase::class.java,
                            "receipt_database"
                        ).fallbackToDestructiveMigration().build()
                    }
                }
            }
            return INSTANCE!!
        }


    }
}
