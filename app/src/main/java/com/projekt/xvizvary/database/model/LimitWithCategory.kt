package com.projekt.xvizvary.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class LimitWithCategory(
    @Embedded
    val limit: Limit,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category
)
