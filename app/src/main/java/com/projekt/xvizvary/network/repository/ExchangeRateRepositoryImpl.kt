package com.projekt.xvizvary.network.repository

import com.projekt.xvizvary.network.ExchangeRateApiService
import com.projekt.xvizvary.network.model.CurrencyNames
import com.projekt.xvizvary.network.model.ExchangeRate
import com.projekt.xvizvary.network.model.ExchangeRateResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRateRepositoryImpl @Inject constructor(
    private val apiService: ExchangeRateApiService
) : ExchangeRateRepository {

    override suspend fun getLatestRates(): ExchangeRateResult {
        return try {
            val response = apiService.getLatestRates(from = "CZK")
            ExchangeRateResult.Success(
                rates = mapResponseToRates(response),
                date = response.date
            )
        } catch (e: Exception) {
            ExchangeRateResult.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getRatesForCurrencies(currencies: List<String>): ExchangeRateResult {
        return try {
            val currencyList = currencies.joinToString(",")
            val response = apiService.getLatestRates(from = "CZK", to = currencyList)
            ExchangeRateResult.Success(
                rates = mapResponseToRates(response),
                date = response.date
            )
        } catch (e: Exception) {
            ExchangeRateResult.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getHistoricalRates(date: String): ExchangeRateResult {
        return try {
            val response = apiService.getHistoricalRates(date = date, from = "CZK")
            ExchangeRateResult.Success(
                rates = mapResponseToRates(response),
                date = response.date
            )
        } catch (e: Exception) {
            ExchangeRateResult.Error(e.message ?: "Unknown error occurred")
        }
    }

    private fun mapResponseToRates(response: ExchangeRateResponse): List<ExchangeRate> {
        return response.rates.map { (code, rate) ->
            ExchangeRate(
                currencyCode = code,
                currencyName = CurrencyNames.getName(code),
                rate = rate,
                inverseRate = if (rate > 0) 1.0 / rate else 0.0
            )
        }.sortedBy { it.currencyCode }
    }
}
