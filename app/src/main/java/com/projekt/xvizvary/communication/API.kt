package com.example.homework2.communication

import com.example.homework2.model.BordersResponse
import com.example.homework2.model.Restaurant
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface API {
    @Headers("Content-Type: application/json")
    @GET("restaurants.json")
    suspend fun getRestaurants(
    ): Response<List<Restaurant>>


    @Headers("Content-Type: application/json")
    @GET("cerna_pole.json")
    suspend fun getBorders(
    ): Response<BordersResponse>
}