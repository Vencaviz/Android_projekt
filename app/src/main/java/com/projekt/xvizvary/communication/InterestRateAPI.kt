package com.projekt.xvizvary.communication

import com.projekt.xvizvary.network.model.InterestRateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface InterestRateAPI {

    companion object {
        const val BASE_URL = "https://api.api-ninjas.com/"
        const val API_KEY = "YOUR_API_KEY_HERE" // Replace with actual API key
    }

    @Headers("Content-Type: application/json")
    @GET("v1/interestrate")
    suspend fun getInterestRates(
        @Header("X-Api-Key") apiKey: String = API_KEY,
        @Query("country") country: String? = null,
        @Query("central_bank_only") centralBankOnly: Boolean? = null
    ): Response<InterestRateResponse>

    @Headers("Content-Type: application/json")
    @GET("v1/interestrate")
    suspend fun getInterestRateByCountry(
        @Header("X-Api-Key") apiKey: String = API_KEY,
        @Query("country") country: String
    ): Response<InterestRateResponse>
}
