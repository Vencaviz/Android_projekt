package com.projekt.xvizvary.di

import android.content.Context
import androidx.room.Room
import com.projekt.xvizvary.database.AppDatabase
import com.projekt.xvizvary.database.CategoryDao
import com.projekt.xvizvary.database.LimitDao
import com.projekt.xvizvary.database.ReceiptDao
import com.projekt.xvizvary.database.TransactionDao
import com.projekt.xvizvary.database.repository.CategoryRepository
import com.projekt.xvizvary.database.repository.CategoryRepositoryImpl
import com.projekt.xvizvary.database.repository.LimitRepository
import com.projekt.xvizvary.database.repository.LimitRepositoryImpl
import com.projekt.xvizvary.database.repository.ReceiptRepository
import com.projekt.xvizvary.database.repository.ReceiptRepositoryImpl
import com.projekt.xvizvary.database.repository.TransactionRepository
import com.projekt.xvizvary.database.repository.TransactionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "smartbudget_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideLimitDao(database: AppDatabase): LimitDao {
        return database.limitDao()
    }

    @Provides
    @Singleton
    fun provideReceiptDao(database: AppDatabase): ReceiptDao {
        return database.receiptDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingsModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindLimitRepository(
        impl: LimitRepositoryImpl
    ): LimitRepository

    @Binds
    @Singleton
    abstract fun bindReceiptRepository(
        impl: ReceiptRepositoryImpl
    ): ReceiptRepository
}
