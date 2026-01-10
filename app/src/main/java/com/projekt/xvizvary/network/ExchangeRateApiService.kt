package com.projekt.xvizvary.network

import com.projekt.xvizvary.network.model.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface ExchangeRateApiService {

    @GET("latest")
    suspend fun getLatestRates(
        @Query("from") from: String = "CZK",
        @Query("to") to: String? = null
    ): ExchangeRateResponse

    @GET("{date}")
    suspend fun getHistoricalRates(
        @retrofit2.http.Path("date") date: String,
        @Query("from") from: String = "CZK"
    ): ExchangeRateResponse

    companion object {
        const val BASE_URL = "https://api.frankfurter.app/"
    }
}
