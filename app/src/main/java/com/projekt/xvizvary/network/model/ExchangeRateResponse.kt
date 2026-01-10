package com.projekt.xvizvary.network.model

import kotlinx.serialization.Serializable

/**
 * Response from Frankfurter API (https://api.frankfurter.app)
 * Example: GET https://api.frankfurter.app/latest?from=CZK
 */
@Serializable
data class ExchangeRateResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
