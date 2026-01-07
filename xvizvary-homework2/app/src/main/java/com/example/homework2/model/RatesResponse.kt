package com.example.homework2.model

import com.squareup.moshi.Json

data class RatesResponse (
    @Json(name = "data")
    val rates: List<ExchangeRate>,

)