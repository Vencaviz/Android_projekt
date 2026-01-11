package com.projekt.xvizvary.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.projekt.xvizvary.ui.screens.auth.SignInScreen
import com.projekt.xvizvary.ui.screens.auth.SignUpScreen
import com.projekt.xvizvary.ui.screens.home.HomeScreen
import com.projekt.xvizvary.ui.screens.limits.LimitDetailScreen
import com.projekt.xvizvary.ui.screens.limits.LimitsScreen
import com.projekt.xvizvary.ui.screens.receipt.ReceiptScanScreen
import com.projekt.xvizvary.ui.screens.search.AtmMapScreen
import com.projekt.xvizvary.ui.screens.search.SearchScreen
import com.projekt.xvizvary.ui.screens.tools.ExchangeRateScreen
import com.projekt.xvizvary.ui.screens.tools.InterestRateScreen
import com.projekt.xvizvary.ui.screens.profile.ProfileScreen
import com.projekt.xvizvary.ui.screens.transaction.AddTransactionScreen

@Composable
fun SmartBudgetRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentRoute = currentDestination?.route

    val isBottomDestination = bottomNavDestinations.any { dest ->
        currentDestination?.hierarchy?.any { it.route == dest.route } == true || currentRoute == dest.route
    }

    val title = destinationTitle(currentRoute)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SmartBudgetTopBar(
                title = title,
                showBack = !isBottomDestination && navController.previousBackStackEntry != null,
                onBack = { navController.navigateUp() }
            )
        },
        bottomBar = {
            if (isBottomDestination) {
                SmartBudgetBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(Destination.Home.route) { saveState = true }
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (isBottomDestination) {
                FloatingActionButton(
                    onClick = { navController.navigate(Destination.ReceiptScan.route) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.SignIn.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Destination.SignIn.route) {
                SignInScreen(
                    onSignUp = { navController.navigate(Destination.SignUp.route) },
                    onSignedIn = {
                        navController.navigate(Destination.Home.route) {
                            popUpTo(Destination.SignIn.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Destination.SignUp.route) {
                SignUpScreen(
                    onSignIn = { navController.navigate(Destination.SignIn.route) },
                    onSignedUp = {
                        navController.navigate(Destination.Home.route) {
                            popUpTo(Destination.SignIn.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Destination.Home.route) {
                HomeScreen(
                    onAddTransaction = { navController.navigate(Destination.AddTransaction.route) }
                )
            }

            composable(Destination.AddTransaction.route) {
                AddTransactionScreen(
                    onTransactionSaved = { navController.navigateUp() },
                    onCancel = { navController.navigateUp() }
                )
            }

            composable(Destination.Search.route) {
                SearchScreen(
                    onAtmMap = { navController.navigate(Destination.AtmMap.route) },
                    onExchangeRate = { navController.navigate(Destination.ExchangeRate.route) },
                    onInterestRate = { navController.navigate(Destination.InterestRate.route) }
                )
            }
            composable(Destination.AtmMap.route) { AtmMapScreen() }
            composable(Destination.ExchangeRate.route) { ExchangeRateScreen() }
            composable(Destination.InterestRate.route) { InterestRateScreen() }

            composable(Destination.Limits.route) {
                LimitsScreen(
                    onLimitClick = { limitId -> 
                        navController.navigate(Destination.LimitDetail.createRoute(limitId)) 
                    },
                    onAddLimit = { 
                        navController.navigate(Destination.LimitDetail.createRoute("0")) 
                    }
                )
            }

            composable(
                route = Destination.LimitDetail.route,
                arguments = listOf(navArgument("limitId") { type = NavType.StringType })
            ) {
                LimitDetailScreen(
                    onLimitSaved = { navController.navigateUp() },
                    onCancel = { navController.navigateUp() }
                )
            }

            composable(Destination.Profile.route) { 
                ProfileScreen(
                    onLoggedOut = {
                        navController.navigate(Destination.SignIn.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(Destination.ReceiptScan.route) { 
                ReceiptScanScreen(
                    onReceiptSaved = { navController.navigateUp() },
                    onCancel = { navController.navigateUp() }
                )
            }
        }
    }
}

@Composable
private fun destinationTitle(route: String?): String {
    val baseRoute = route?.substringBefore("/")
    val resId = when (baseRoute) {
        "sign_in" -> Destination.SignIn.titleRes
        "sign_up" -> Destination.SignUp.titleRes
        "home" -> Destination.Home.titleRes
        "search" -> Destination.Search.titleRes
        "limits" -> Destination.Limits.titleRes
        "profile" -> Destination.Profile.titleRes
        "limit_detail" -> Destination.LimitDetail.titleRes
        "add_transaction" -> Destination.AddTransaction.titleRes
        "exchange_rate" -> Destination.ExchangeRate.titleRes
        "interest_rate" -> Destination.InterestRate.titleRes
        "atm_map" -> Destination.AtmMap.titleRes
        "receipt_scan" -> Destination.ReceiptScan.titleRes
        else -> null
    }
    return if (resId != null) stringResource(resId) else ""
}

