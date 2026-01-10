package com.projekt.xvizvary.database.repository

import com.projekt.xvizvary.database.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategoriesByUser(userId: String): Flow<List<Category>>
    suspend fun getCategoriesByUserOnce(userId: String): List<Category>
    suspend fun getCategoryById(id: Long): Category?
    suspend fun getCategoryByFirestoreId(firestoreId: String): Category?
    suspend fun getCategoryByUserAndName(userId: String, name: String): Category?
    suspend fun insertCategory(category: Category): Long
    suspend fun insertCategories(categories: List<Category>)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    suspend fun getCategoryCountByUser(userId: String): Int
    suspend fun deleteAllByUser(userId: String)
}
