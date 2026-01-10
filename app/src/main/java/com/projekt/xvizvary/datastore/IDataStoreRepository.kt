package com.projekt.xvizvary.datastore

interface IDataStoreRepository {
    suspend fun setLoginSuccessful()
    suspend fun getLoginSuccessful(): Boolean
}
