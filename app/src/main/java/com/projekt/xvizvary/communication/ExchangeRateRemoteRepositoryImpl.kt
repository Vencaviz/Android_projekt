package com.projekt.xvizvary.communication

import com.projekt.xvizvary.network.model.CurrencyNames
import com.projekt.xvizvary.network.model.ExchangeRate
import com.projekt.xvizvary.network.model.ExchangeRateResponse
import javax.inject.Inject

class ExchangeRateRemoteRepositoryImpl @Inject constructor(
    private val api: ExchangeRateAPI
) : IExchangeRateRemoteRepository {

    override suspend fun getLatestRates(): CommunicationResult<List<ExchangeRate>> {
        return when (val result = processResponse { api.getLatestRates() }) {
            is CommunicationResult.Success -> {
                CommunicationResult.Success(mapResponseToRates(result.data))
            }
            is CommunicationResult.Error -> result
            is CommunicationResult.ConnectionError -> result
            is CommunicationResult.Exception -> result
        }
    }

    override suspend fun getRatesForCurrencies(currencies: List<String>): CommunicationResult<List<ExchangeRate>> {
        val currenciesParam = currencies.joinToString(",")
        return when (val result = processResponse { api.getLatestRates(to = currenciesParam) }) {
            is CommunicationResult.Success -> {
                CommunicationResult.Success(mapResponseToRates(result.data))
            }
            is CommunicationResult.Error -> result
            is CommunicationResult.ConnectionError -> result
            is CommunicationResult.Exception -> result
        }
    }

    override suspend fun getHistoricalRates(date: String): CommunicationResult<List<ExchangeRate>> {
        return when (val result = processResponse { api.getHistoricalRates(date) }) {
            is CommunicationResult.Success -> {
                CommunicationResult.Success(mapResponseToRates(result.data))
            }
            is CommunicationResult.Error -> result
            is CommunicationResult.ConnectionError -> result
            is CommunicationResult.Exception -> result
        }
    }

    private fun mapResponseToRates(response: ExchangeRateResponse): List<ExchangeRate> {
        return response.rates.map { (currencyCode, rate) ->
            ExchangeRate(
                currencyCode = currencyCode,
                currencyName = CurrencyNames.getName(currencyCode),
                rate = rate,
                inverseRate = if (rate > 0) 1.0 / rate else 0.0
            )
        }.sortedBy { it.currencyCode }
    }
}
