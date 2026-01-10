package com.projekt.xvizvary.network.repository

import com.projekt.xvizvary.network.model.ExchangeRate

sealed class ExchangeRateResult {
    data class Success(val rates: List<ExchangeRate>, val date: String) : ExchangeRateResult()
    data class Error(val message: String) : ExchangeRateResult()
    data object Loading : ExchangeRateResult()
}

interface ExchangeRateRepository {

    suspend fun getLatestRates(): ExchangeRateResult

    suspend fun getRatesForCurrencies(currencies: List<String>): ExchangeRateResult

    suspend fun getHistoricalRates(date: String): ExchangeRateResult
}
