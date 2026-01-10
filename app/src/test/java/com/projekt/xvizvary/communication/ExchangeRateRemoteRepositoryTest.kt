package com.projekt.xvizvary.communication

import com.projekt.xvizvary.network.model.ExchangeRateResponse
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

class ExchangeRateRemoteRepositoryTest {

    private lateinit var api: ExchangeRateAPI
    private lateinit var repository: ExchangeRateRemoteRepositoryImpl

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
        api = mock()
        repository = ExchangeRateRemoteRepositoryImpl(api)
    }

    @Test
    fun `getLatestRates returns success with rates`() = runTest {
        whenever(api.getLatestRates("CZK", null))
            .thenReturn(Response.success(testResponse))

        val result = repository.getLatestRates()

        assertTrue(result is CommunicationResult.Success)
        val success = result as CommunicationResult.Success
        assertEquals(3, success.data.size)
    }

    @Test
    fun `getLatestRates returns sorted rates by currency code`() = runTest {
        whenever(api.getLatestRates("CZK", null))
            .thenReturn(Response.success(testResponse))

        val result = repository.getLatestRates()

        assertTrue(result is CommunicationResult.Success)
        val success = result as CommunicationResult.Success
        assertEquals("EUR", success.data[0].currencyCode)
        assertEquals("GBP", success.data[1].currencyCode)
        assertEquals("USD", success.data[2].currencyCode)
    }

    @Test
    fun `getLatestRates calculates inverse rate correctly`() = runTest {
        whenever(api.getLatestRates("CZK", null))
            .thenReturn(Response.success(testResponse))

        val result = repository.getLatestRates()

        assertTrue(result is CommunicationResult.Success)
        val success = result as CommunicationResult.Success
        val eurRate = success.data.find { it.currencyCode == "EUR" }
        assertNotNull(eurRate)
        // 1 / 0.041 ≈ 24.39
        assertEquals(24.39, eurRate!!.inverseRate, 0.1)
    }

    @Test
    fun `getLatestRates returns error on HTTP error`() = runTest {
        whenever(api.getLatestRates("CZK", null))
            .thenReturn(Response.error(404, "Not found".toResponseBody()))

        val result = repository.getLatestRates()

        assertTrue(result is CommunicationResult.Error)
        val error = result as CommunicationResult.Error
        assertEquals(404, error.error.code)
    }

    @Test
    fun `getRatesForCurrencies filters currencies correctly`() = runTest {
        val filteredResponse = testResponse.copy(
            rates = mapOf("EUR" to 0.041, "USD" to 0.045)
        )
        whenever(api.getLatestRates("CZK", "EUR,USD"))
            .thenReturn(Response.success(filteredResponse))

        val result = repository.getRatesForCurrencies(listOf("EUR", "USD"))

        assertTrue(result is CommunicationResult.Success)
        val success = result as CommunicationResult.Success
        assertEquals(2, success.data.size)
    }

    @Test
    fun `getHistoricalRates returns rates for specific date`() = runTest {
        whenever(api.getHistoricalRates("2024-01-01", "CZK"))
            .thenReturn(Response.success(testResponse))

        val result = repository.getHistoricalRates("2024-01-01")

        assertTrue(result is CommunicationResult.Success)
    }

    @Test
    fun `currency names are mapped correctly`() = runTest {
        whenever(api.getLatestRates("CZK", null))
            .thenReturn(Response.success(testResponse))

        val result = repository.getLatestRates()

        assertTrue(result is CommunicationResult.Success)
        val success = result as CommunicationResult.Success
        val eurRate = success.data.find { it.currencyCode == "EUR" }
        assertEquals("Euro", eurRate?.currencyName)
    }
}
