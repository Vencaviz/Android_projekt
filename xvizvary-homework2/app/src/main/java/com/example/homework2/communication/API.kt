package com.example.homework2.communication

import com.example.homework2.model.BordersResponse
import com.example.homework2.model.RatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface API {
    @Headers("Content-Type: application/json")
    @GET("v1/accounting/od/rates_of_exchange")
    suspend fun getRates(
    ): Response<RatesResponse>





}