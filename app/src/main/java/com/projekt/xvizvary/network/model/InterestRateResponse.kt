package com.projekt.xvizvary.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterestRateResponse(
    @SerialName("central_bank_rates")
    val centralBankRates: List<CentralBankRate>,
    @SerialName("non_central_bank_rates")
    val nonCentralBankRates: List<NonCentralBankRate>? = null
)

@Serializable
data class CentralBankRate(
    @SerialName("central_bank")
    val centralBank: String,
    val country: String,
    @SerialName("rate_pct")
    val ratePct: Double,
    @SerialName("last_updated")
    val lastUpdated: String
)

@Serializable
data class NonCentralBankRate(
    val name: String,
    @SerialName("rate_pct")
    val ratePct: Double,
    @SerialName("last_updated")
    val lastUpdated: String
)

data class InterestRateDisplay(
    val id: String,
    val name: String,
    val country: String,
    val ratePct: Double,
    val lastUpdated: String,
    val isCentralBank: Boolean
) {
    val formattedRate: String
        get() = String.format("%.2f%%", ratePct)
}
