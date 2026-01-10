package com.projekt.xvizvary.ui.navigation

import androidx.annotation.StringRes
import com.projekt.xvizvary.R

sealed class Destination(
    val route: String,
    @StringRes val titleRes: Int? = null
) {
    data object SignIn : Destination("sign_in", R.string.screen_sign_in)
    data object SignUp : Destination("sign_up", R.string.screen_sign_up)

    data object Home : Destination("home", R.string.screen_overview)
    data object Search : Destination("search", R.string.screen_search)
    data object Limits : Destination("limits", R.string.screen_limits)
    data object Profile : Destination("profile", R.string.screen_profile)

    data object LimitDetail : Destination("limit_detail", R.string.screen_limit_detail)
    data object ExchangeRate : Destination("exchange_rate", R.string.screen_exchange_rate)
    data object InterestRate : Destination("interest_rate", R.string.screen_interest_rate)
    data object AtmMap : Destination("atm_map", R.string.screen_search)
    data object ReceiptScan : Destination("receipt_scan", R.string.action_scan_receipt)
}

val bottomNavDestinations = listOf(
    Destination.Home,
    Destination.Search,
    Destination.Limits,
    Destination.Profile
)

