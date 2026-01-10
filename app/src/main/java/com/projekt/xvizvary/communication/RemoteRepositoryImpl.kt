package com.example.homework2.communication

import com.example.homework2.model.Border
import com.example.homework2.model.Restaurant
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(

    private val api: API) : IAPIRemoteRepository{

    override suspend fun getRestaurants(): CommunicationResult<List<Restaurant>> {
        return when (val result = processResponse { api.getRestaurants() }) {
            is CommunicationResult.Success -> {
                CommunicationResult.Success(result.data)
            }
            is CommunicationResult.Error -> result
            is CommunicationResult.ConnectionError -> result
            is CommunicationResult.Exception -> result
        }
    }

    override suspend fun getBorders(): CommunicationResult<List<Border>> {
        return when (val result = processResponse { api.getBorders() }) {
            is CommunicationResult.Success -> {
                CommunicationResult.Success(result.data.features)
            }
            is CommunicationResult.Error -> result
            is CommunicationResult.ConnectionError -> result
            is CommunicationResult.Exception -> result
        }
    }

}