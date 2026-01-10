package com.projekt.xvizvary.network.repository

import com.projekt.xvizvary.network.model.ExchangeRate

sealed class ExchangeRateResult {
    data class Success(val rates: List<ExchangeRate>, val date: String) : ExchangeRateResult()
    data class Error(val message: String) : ExchangeRateResult()
    data object Loading : ExchangeRateResult()
}

interface ExchangeRateRepository {

    /**
     * Get latest exchange rates from CZK to major currencies
     */
    suspend fun getLatestRates(): ExchangeRateResult

    /**
     * Get exchange rates for specific currencies
     * @param currencies List of currency codes (e.g., ["EUR", "USD", "GBP"])
     */
    suspend fun getRatesForCurrencies(currencies: List<String>): ExchangeRateResult

    /**
     * Get historical rates for a specific date
     * @param date Date in YYYY-MM-DD format
     */
    suspend fun getHistoricalRates(date: String): ExchangeRateResult
}
