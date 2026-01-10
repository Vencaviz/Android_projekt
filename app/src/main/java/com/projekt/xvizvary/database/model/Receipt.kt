package com.projekt.xvizvary.database.model


import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "receipts")
data class Receipt(
    var Store: String,
    var type: String,
    var amount: String,

    ) {
    @PrimaryKey(autoGenerate = true)
    var id:Long? = null


}
