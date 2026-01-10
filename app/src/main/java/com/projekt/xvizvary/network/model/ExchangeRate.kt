package com.projekt.xvizvary.network.model

/**
 * UI model for displaying exchange rates
 */
data class ExchangeRate(
    val currencyCode: String,
    val currencyName: String,
    val rate: Double,
    val inverseRate: Double // How much CZK for 1 unit of foreign currency
) {
    val formattedRate: String
        get() = String.format("%.4f", rate)

    val formattedInverseRate: String
        get() = String.format("%.2f", inverseRate)
}

/**
 * Common currency names for display
 */
object CurrencyNames {
    private val names = mapOf(
        "EUR" to "Euro",
        "USD" to "US Dollar",
        "GBP" to "British Pound",
        "CHF" to "Swiss Franc",
        "JPY" to "Japanese Yen",
        "PLN" to "Polish Zloty",
        "HUF" to "Hungarian Forint",
        "SEK" to "Swedish Krona",
        "NOK" to "Norwegian Krone",
        "DKK" to "Danish Krone",
        "AUD" to "Australian Dollar",
        "CAD" to "Canadian Dollar",
        "CNY" to "Chinese Yuan",
        "KRW" to "South Korean Won",
        "INR" to "Indian Rupee",
        "BRL" to "Brazilian Real",
        "MXN" to "Mexican Peso",
        "RUB" to "Russian Ruble",
        "TRY" to "Turkish Lira",
        "ZAR" to "South African Rand",
        "NZD" to "New Zealand Dollar",
        "SGD" to "Singapore Dollar",
        "HKD" to "Hong Kong Dollar",
        "THB" to "Thai Baht",
        "MYR" to "Malaysian Ringgit",
        "PHP" to "Philippine Peso",
        "IDR" to "Indonesian Rupiah",
        "ISK" to "Icelandic Krona",
        "BGN" to "Bulgarian Lev",
        "RON" to "Romanian Leu",
        "ILS" to "Israeli Shekel",
        "CZK" to "Czech Koruna"
    )

    fun getName(code: String): String = names[code] ?: code
}
