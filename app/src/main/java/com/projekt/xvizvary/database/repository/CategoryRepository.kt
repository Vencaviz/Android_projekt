package com.projekt.xvizvary.database.repository

import com.projekt.xvizvary.database.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    fun getAllCategories(): Flow<List<Category>>

    suspend fun getAllCategoriesOnce(): List<Category>

    suspend fun getCategoryById(id: Long): Category?

    suspend fun getCategoryByName(name: String): Category?

    suspend fun insertCategory(category: Category): Long

    suspend fun insertCategories(categories: List<Category>)

    suspend fun updateCategory(category: Category)

    suspend fun deleteCategory(category: Category)

    suspend fun getCategoryCount(): Int

    suspend fun deleteAllCategories()

    suspend fun initializeDefaultCategories()
}
