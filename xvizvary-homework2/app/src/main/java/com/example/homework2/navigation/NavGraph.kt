package com.example.homework2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel // Důležitý import pro Hilt!
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.homework2.ui.auth.login.LoginScreen
import com.example.homework2.ui.auth.login.LoginViewModel
import com.example.homework2.ui.auth.register.RegistrationScreen
import com.example.homework2.ui.auth.register.RegistrationViewModel
import com.example.homework2.ui.components.LoadingScreen
import com.example.homework2.ui.screens.mainDashboard.MainDashboardScreen
import com.example.homework2.ui.screens.mainDashboard.MainDashboardViewModel // Předpokládám existenci

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    // Router ponecháme, pokud ho používáš pro Mapu/Detaily
    navigationRouter: INavigationRouter = remember {
        NavigationRouterImpl(navController)
    },
    // StartDestination už nemusíme předávat zvenčí ručně,
    // vypočítáme si ho uvnitř pomocí MainViewModelu
) {
    // Získáme MainViewModel pomocí Hiltu pro sledování stavu přihlášení
    val mainViewModel: com.example.homework2.ui.activity.MainViewModel = hiltViewModel()
    val isLoggedIn by mainViewModel.authState.collectAsState()

    // 1. Fáze: Zjišťování stavu (Loading)
    if (isLoggedIn == null) {
        LoadingScreen()
        return
    }

    // 2. Fáze: Samotná navigace
    NavHost(
        navController = navController,
        // Pokud je loggedIn true, startujeme v hlavní sekci, jinak v auth
        startDestination = if (isLoggedIn == true) "main_section" else "auth_section"
    ) {
        // Graf pro NEPŘIHLÁŠENÉ (Auth)
        navigation(startDestination = "login", route = "auth_section") {
            composable("login") {
                val loginVM: LoginViewModel = hiltViewModel()
                LoginScreen(
                    viewModel = loginVM,
                    onLoginSuccess = {
                        navController.navigate("main_section") {
                            popUpTo("auth_section") { inclusive = true }
                        }
                    },
                    onRegisterClick = { // Přidal jsem pro navigaci na registraci
                        navController.navigate("register")
                    },
                    navigation = navController
                )
            }

            composable("register") {
                val registerVM: RegistrationViewModel = hiltViewModel()
                RegistrationScreen(
                    viewModel = registerVM,
                    onRegistrationSuccess = {
                        navController.navigate("main_section") {
                            popUpTo("auth_section") { inclusive = true }
                        }
                    }
                )
            }
        }

        // Graf pro PŘIHLÁŠENÉ (Main)
        navigation(startDestination = "dashboard", route = "main_section") {
            composable("dashboard") {
                // MainDashboardViewModel by měl mít v konstruktoru Repository pro signOut
                val dashboardVM: MainDashboardViewModel = hiltViewModel()

                MainDashboardScreen(
                    onLogout = {
                        dashboardVM.logout() // ViewModel zavolá repository.signOut()
                        navController.navigate("auth_section") {
                            popUpTo("main_section") { inclusive = true }
                        }
                    },
                    navigation = navigationRouter
                )
            }
        }
    }
}