package com.projekt.xvizvary.communication

import com.projekt.xvizvary.network.model.ExchangeRate

interface IExchangeRateRemoteRepository : IBaseRemoteRepository {

    suspend fun getLatestRates(): CommunicationResult<List<ExchangeRate>>

    suspend fun getRatesForCurrencies(currencies: List<String>): CommunicationResult<List<ExchangeRate>>

    suspend fun getHistoricalRates(date: String): CommunicationResult<List<ExchangeRate>>
}
