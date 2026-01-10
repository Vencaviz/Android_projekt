package com.projekt.xvizvary.network

import com.projekt.xvizvary.network.model.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API service for Frankfurter Exchange Rate API
 * Documentation: https://www.frankfurter.app/docs/
 */
interface ExchangeRateApiService {

    /**
     * Get latest exchange rates
     * @param from Base currency code (default: CZK)
     * @param to Comma-separated list of target currencies (optional, returns all if not specified)
     */
    @GET("latest")
    suspend fun getLatestRates(
        @Query("from") from: String = "CZK",
        @Query("to") to: String? = null
    ): ExchangeRateResponse

    /**
     * Get historical exchange rates for a specific date
     * @param date Date in YYYY-MM-DD format
     * @param from Base currency code
     */
    @GET("{date}")
    suspend fun getHistoricalRates(
        @retrofit2.http.Path("date") date: String,
        @Query("from") from: String = "CZK"
    ): ExchangeRateResponse

    companion object {
        const val BASE_URL = "https://api.frankfurter.app/"
    }
}
