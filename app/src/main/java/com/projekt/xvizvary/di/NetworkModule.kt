package com.projekt.xvizvary.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.projekt.xvizvary.communication.ExchangeRateAPI
import com.projekt.xvizvary.communication.ExchangeRateRemoteRepositoryImpl
import com.projekt.xvizvary.communication.IExchangeRateRemoteRepository
import com.projekt.xvizvary.communication.IInterestRateRemoteRepository
import com.projekt.xvizvary.communication.InterestRateAPI
import com.projekt.xvizvary.communication.InterestRateRemoteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("exchangeRate")
    fun provideExchangeRateRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(ExchangeRateAPI.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    @Named("interestRate")
    fun provideInterestRateRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(InterestRateAPI.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideExchangeRateAPI(@Named("exchangeRate") retrofit: Retrofit): ExchangeRateAPI {
        return retrofit.create(ExchangeRateAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideInterestRateAPI(@Named("interestRate") retrofit: Retrofit): InterestRateAPI {
        return retrofit.create(InterestRateAPI::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindingsModule {

    @Binds
    @Singleton
    abstract fun bindExchangeRateRemoteRepository(
        impl: ExchangeRateRemoteRepositoryImpl
    ): IExchangeRateRemoteRepository

    @Binds
    @Singleton
    abstract fun bindInterestRateRemoteRepository(
        impl: InterestRateRemoteRepositoryImpl
    ): IInterestRateRemoteRepository
}
