package com.example.homework2.communication

import com.example.homework2.model.Border
import com.example.homework2.model.ExchangeRate

interface IAPIRemoteRepository : IBaseRemoteRepository {
    suspend fun getRates(): CommunicationResult<List<ExchangeRate>>


}