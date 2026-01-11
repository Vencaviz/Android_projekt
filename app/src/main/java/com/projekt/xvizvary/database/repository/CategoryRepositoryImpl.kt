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

    override fun getCategoriesByUser(userId: String): Flow<List<Category>> {
        return categoryDao.getAllByUser(userId)
    }

    override suspend fun getCategoriesByUserOnce(userId: String): List<Category> {
        return categoryDao.getAllByUserOnce(userId)
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getById(id)
    }

    override suspend fun getCategoryByFirestoreId(firestoreId: String): Category? {
        return categoryDao.getByFirestoreId(firestoreId)
    }

    override suspend fun getCategoryByUserAndName(userId: String, name: String): Category? {
        return categoryDao.getByUserAndName(userId, name)
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

    override suspend fun getCategoryCountByUser(userId: String): Int {
        return categoryDao.getCountByUser(userId)
    }

    override suspend fun deleteAllByUser(userId: String) {
        categoryDao.deleteAllByUser(userId)
    }
}
