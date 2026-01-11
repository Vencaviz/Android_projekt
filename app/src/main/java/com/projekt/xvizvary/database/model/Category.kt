package com.projekt.xvizvary.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["firestoreId"], unique = true)
    ]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firestoreId: String = "", // ID from Firestore for sync
    val userId: String = "", // Owner of this category
    val name: String,
    val icon: String, // Material icon name
    val color: Long, // Color as ARGB long
    val isDefault: Boolean = false
)
