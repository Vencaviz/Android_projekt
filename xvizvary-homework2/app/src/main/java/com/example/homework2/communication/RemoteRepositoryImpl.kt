package com.example.homework2.communication

import com.example.homework2.model.Border
import com.example.homework2.model.ExchangeRate
import com.example.homework2.model.RatesResponse

import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(

    private val api: API) : IAPIRemoteRepository{

    override suspend fun getRates(): CommunicationResult<List<ExchangeRate>> {
        return when (val result = processResponse { api.getRates() }) {
            is CommunicationResult.Success -> {
                CommunicationResult.Success(result.data.rates)
            }
            is CommunicationResult.Error -> result
            is CommunicationResult.ConnectionError -> result
            is CommunicationResult.Exception -> result
        }
    }

}