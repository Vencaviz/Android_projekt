package com.projekt.xvizvary.network.repository

import com.projekt.xvizvary.network.ExchangeRateApiService
import com.projekt.xvizvary.network.model.ExchangeRateResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ExchangeRateRepositoryTest {

    private lateinit var apiService: ExchangeRateApiService
    private lateinit var repository: ExchangeRateRepositoryImpl

    private val testResponse = ExchangeRateResponse(
        amount = 1.0,
        base = "CZK",
        date = "2024-01-15",
        rates = mapOf(
            "EUR" to 0.041,
            "USD" to 0.045,
            "GBP" to 0.035
        )
    )

    @Before
    fun setup() {
        apiService = mock()
        repository = ExchangeRateRepositoryImpl(apiService)
    }

    @Test
    fun `getLatestRates returns success with rates`() = runTest {
        whenever(apiService.getLatestRates("CZK", null)).thenReturn(testResponse)

        val result = repository.getLatestRates()

        assertTrue(result is ExchangeRateResult.Success)
        val success = result as ExchangeRateResult.Success
        assertEquals(3, success.rates.size)
        assertEquals("2024-01-15", success.date)
    }

    @Test
    fun `getLatestRates returns sorted rates by currency code`() = runTest {
        whenever(apiService.getLatestRates("CZK", null)).thenReturn(testResponse)

        val result = repository.getLatestRates()

        assertTrue(result is ExchangeRateResult.Success)
        val success = result as ExchangeRateResult.Success
        assertEquals("EUR", success.rates[0].currencyCode)
        assertEquals("GBP", success.rates[1].currencyCode)
        assertEquals("USD", success.rates[2].currencyCode)
    }

    @Test
    fun `getLatestRates calculates inverse rate correctly`() = runTest {
        whenever(apiService.getLatestRates("CZK", null)).thenReturn(testResponse)

        val result = repository.getLatestRates()

        assertTrue(result is ExchangeRateResult.Success)
        val success = result as ExchangeRateResult.Success
        val eurRate = success.rates.find { it.currencyCode == "EUR" }
        assertNotNull(eurRate)
        // 1 / 0.041 ≈ 24.39
        assertEquals(24.39, eurRate!!.inverseRate, 0.1)
    }

    @Test
    fun `getLatestRates returns error on exception`() = runTest {
        whenever(apiService.getLatestRates("CZK", null))
            .thenThrow(RuntimeException("Network error"))

        val result = repository.getLatestRates()

        assertTrue(result is ExchangeRateResult.Error)
        val error = result as ExchangeRateResult.Error
        assertEquals("Network error", error.message)
    }

    @Test
    fun `getRatesForCurrencies filters currencies correctly`() = runTest {
        val filteredResponse = testResponse.copy(
            rates = mapOf("EUR" to 0.041, "USD" to 0.045)
        )
        whenever(apiService.getLatestRates("CZK", "EUR,USD")).thenReturn(filteredResponse)

        val result = repository.getRatesForCurrencies(listOf("EUR", "USD"))

        assertTrue(result is ExchangeRateResult.Success)
        val success = result as ExchangeRateResult.Success
        assertEquals(2, success.rates.size)
    }

    @Test
    fun `getHistoricalRates returns rates for specific date`() = runTest {
        whenever(apiService.getHistoricalRates("2024-01-01", "CZK")).thenReturn(testResponse)

        val result = repository.getHistoricalRates("2024-01-01")

        assertTrue(result is ExchangeRateResult.Success)
    }

    @Test
    fun `currency names are mapped correctly`() = runTest {
        whenever(apiService.getLatestRates("CZK", null)).thenReturn(testResponse)

        val result = repository.getLatestRates()

        assertTrue(result is ExchangeRateResult.Success)
        val success = result as ExchangeRateResult.Success
        val eurRate = success.rates.find { it.currencyCode == "EUR" }
        assertEquals("Euro", eurRate?.currencyName)
    }
}
