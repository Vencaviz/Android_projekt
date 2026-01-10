package com.projekt.xvizvary.network.model

import kotlinx.serialization.Serializable


@Serializable
data class ExchangeRateResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
