package com.example.homework2.di

import com.example.homework2.communication.API
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun providesAPI(retrofit: Retrofit): API {
        return retrofit.create(API::class.java)
    }

}