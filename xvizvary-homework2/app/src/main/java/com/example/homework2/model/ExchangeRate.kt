package com.example.homework2.model

data class ExchangeRate (
    val record_date: String? = null,
    val country: String? = null,
    val currency: String? = null,
    val country_currency_desc: String? = null,
    val exchange_rate: Double? = null,
    val effective_date: String? = null,
    val src_line_nbr: Int? = null,
    val record_fiscal_year: Int? = null,
    val record_fiscal_quarter: Int? = null,
    val record_calendar_year: Int? = null,
    val record_calendar_quarter: Int? = null,
    val record_calendar_month: Int? = null,
    val record_calendar_day: Int? = null
)


