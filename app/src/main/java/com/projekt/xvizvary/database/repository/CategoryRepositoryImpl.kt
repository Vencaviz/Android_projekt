package com.projekt.xvizvary.database.repository

import com.projekt.xvizvary.database.CategoryDao
import com.projekt.xvizvary.database.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAll()
    }

    override suspend fun getAllCategoriesOnce(): List<Category> {
        return categoryDao.getAllOnce()
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getById(id)
    }

    override suspend fun getCategoryByName(name: String): Category? {
        return categoryDao.getByName(name)
    }

    override suspend fun insertCategory(category: Category): Long {
        return categoryDao.insert(category)
    }

    override suspend fun insertCategories(categories: List<Category>) {
        categoryDao.insertAll(categories)
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.update(category)
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category)
    }

    override suspend fun getCategoryCount(): Int {
        return categoryDao.getCount()
    }

    override suspend fun deleteAllCategories() {
        categoryDao.deleteAll()
    }

    override suspend fun initializeDefaultCategories() {
        if (getCategoryCount() == 0) {
            val defaultCategories = listOf(
                Category(name = "Food", icon = "restaurant", color = 0xFFE57373),
                Category(name = "Transport", icon = "directions_car", color = 0xFF64B5F6),
                Category(name = "Shopping", icon = "shopping_bag", color = 0xFFBA68C8),
                Category(name = "Entertainment", icon = "movie", color = 0xFFFFB74D),
                Category(name = "Bills", icon = "receipt", color = 0xFF4DB6AC),
                Category(name = "Health", icon = "medical_services", color = 0xFFEF5350),
                Category(name = "Salary", icon = "payments", color = 0xFF81C784),
                Category(name = "Other", icon = "more_horiz", color = 0xFF90A4AE)
            )
            insertCategories(defaultCategories)
        }
    }
}
