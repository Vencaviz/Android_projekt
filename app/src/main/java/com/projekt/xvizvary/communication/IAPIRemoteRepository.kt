package com.example.homework2.communication

import com.example.homework2.model.Border
import com.example.homework2.model.Restaurant

interface IAPIRemoteRepository : IBaseRemoteRepository {
    suspend fun getRestaurants(): CommunicationResult<List<Restaurant>>

    suspend fun getBorders() : CommunicationResult<List<Border>>
}