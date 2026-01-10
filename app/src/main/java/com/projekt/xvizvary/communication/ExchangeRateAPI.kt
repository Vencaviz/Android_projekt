package com.projekt.xvizvary.communication

import com.projekt.xvizvary.network.model.ExchangeRateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ExchangeRateAPI {

    companion object {
        const val BASE_URL = "https://api.frankfurter.app/"
    }

    @Headers("Content-Type: application/json")
    @GET("latest")
    suspend fun getLatestRates(
        @Query("from") from: String = "CZK",
        @Query("to") to: String? = null
    ): Response<ExchangeRateResponse>

    @Headers("Content-Type: application/json")
    @GET("{date}")
    suspend fun getHistoricalRates(
        @Path("date") date: String,
        @Query("from") from: String = "CZK"
    ): Response<ExchangeRateResponse>
}
