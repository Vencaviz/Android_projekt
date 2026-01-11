package com.projekt.xvizvary.network.model

data class InterestRateDisplay(
    val id: String,
    val name: String,
    val country: String,
    val countryCode: String,
    val ratePct: Double,
    val previousRatePct: Double,
    val lastUpdated: String,
    val isCentralBank: Boolean = true
) {
    val formattedRate: String
        get() = String.format("%.2f%%", ratePct)

    val change: Double
        get() = ratePct - previousRatePct

    val formattedChange: String
        get() = if (change >= 0) "+${String.format("%.2f", change)}%" else "${String.format("%.2f", change)}%"

    val isIncreasing: Boolean
        get() = change > 0
}

object CentralBankData {

    fun getAllRates(): List<InterestRateDisplay> = listOf(
        InterestRateDisplay(
            id = "fed",
            name = "Federal Reserve",
            country = "United States",
            countryCode = "US",
            ratePct = 5.50,
            previousRatePct = 5.25,
            lastUpdated = "2024-01-31"
        ),
        InterestRateDisplay(
            id = "ecb",
            name = "European Central Bank",
            country = "Eurozone",
            countryCode = "EU",
            ratePct = 4.50,
            previousRatePct = 4.50,
            lastUpdated = "2024-01-25"
        ),
        InterestRateDisplay(
            id = "cnb",
            name = "Czech National Bank",
            country = "Czech Republic",
            countryCode = "CZ",
            ratePct = 6.75,
            previousRatePct = 7.00,
            lastUpdated = "2024-02-01"
        ),
        InterestRateDisplay(
            id = "boe",
            name = "Bank of England",
            country = "United Kingdom",
            countryCode = "GB",
            ratePct = 5.25,
            previousRatePct = 5.25,
            lastUpdated = "2024-02-01"
        ),
        InterestRateDisplay(
            id = "snb",
            name = "Swiss National Bank",
            country = "Switzerland",
            countryCode = "CH",
            ratePct = 1.75,
            previousRatePct = 1.75,
            lastUpdated = "2024-01-25"
        ),
        InterestRateDisplay(
            id = "boj",
            name = "Bank of Japan",
            country = "Japan",
            countryCode = "JP",
            ratePct = -0.10,
            previousRatePct = -0.10,
            lastUpdated = "2024-01-23"
        ),
        InterestRateDisplay(
            id = "rba",
            name = "Reserve Bank of Australia",
            country = "Australia",
            countryCode = "AU",
            ratePct = 4.35,
            previousRatePct = 4.35,
            lastUpdated = "2024-02-06"
        ),
        InterestRateDisplay(
            id = "boc",
            name = "Bank of Canada",
            country = "Canada",
            countryCode = "CA",
            ratePct = 5.00,
            previousRatePct = 5.00,
            lastUpdated = "2024-01-24"
        ),
        InterestRateDisplay(
            id = "pboc",
            name = "People's Bank of China",
            country = "China",
            countryCode = "CN",
            ratePct = 3.45,
            previousRatePct = 3.45,
            lastUpdated = "2024-01-22"
        ),
        InterestRateDisplay(
            id = "rbi",
            name = "Reserve Bank of India",
            country = "India",
            countryCode = "IN",
            ratePct = 6.50,
            previousRatePct = 6.50,
            lastUpdated = "2024-02-08"
        ),
        InterestRateDisplay(
            id = "bcb",
            name = "Central Bank of Brazil",
            country = "Brazil",
            countryCode = "BR",
            ratePct = 11.25,
            previousRatePct = 11.75,
            lastUpdated = "2024-01-31"
        ),
        InterestRateDisplay(
            id = "nbp",
            name = "National Bank of Poland",
            country = "Poland",
            countryCode = "PL",
            ratePct = 5.75,
            previousRatePct = 5.75,
            lastUpdated = "2024-02-07"
        ),
        InterestRateDisplay(
            id = "riksbank",
            name = "Sveriges Riksbank",
            country = "Sweden",
            countryCode = "SE",
            ratePct = 4.00,
            previousRatePct = 4.00,
            lastUpdated = "2024-01-31"
        ),
        InterestRateDisplay(
            id = "norges",
            name = "Norges Bank",
            country = "Norway",
            countryCode = "NO",
            ratePct = 4.50,
            previousRatePct = 4.50,
            lastUpdated = "2024-01-25"
        ),
        InterestRateDisplay(
            id = "rbnz",
            name = "Reserve Bank of New Zealand",
            country = "New Zealand",
            countryCode = "NZ",
            ratePct = 5.50,
            previousRatePct = 5.50,
            lastUpdated = "2024-02-28"
        )
    )

    fun getHistoricalData(rateId: String): List<HistoricalRate> {
        val baseRate = getAllRates().find { it.id == rateId }?.ratePct ?: 5.0
        
        return listOf(
            HistoricalRate("Jan 2023", baseRate - 2.0),
            HistoricalRate("Feb 2023", baseRate - 1.8),
            HistoricalRate("Mar 2023", baseRate - 1.5),
            HistoricalRate("Apr 2023", baseRate - 1.2),
            HistoricalRate("May 2023", baseRate - 1.0),
            HistoricalRate("Jun 2023", baseRate - 0.7),
            HistoricalRate("Jul 2023", baseRate - 0.5),
            HistoricalRate("Aug 2023", baseRate - 0.3),
            HistoricalRate("Sep 2023", baseRate - 0.2),
            HistoricalRate("Oct 2023", baseRate - 0.1),
            HistoricalRate("Nov 2023", baseRate - 0.05),
            HistoricalRate("Dec 2023", baseRate)
        ).map { it.copy(rate = it.rate.coerceAtLeast(-0.5)) }
    }
}

data class HistoricalRate(
    val label: String,
    val rate: Double
)
