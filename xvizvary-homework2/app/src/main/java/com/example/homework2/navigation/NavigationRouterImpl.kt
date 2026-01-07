package com.example.homework2.navigation

import androidx.navigation.NavController

class NavigationRouterImpl(private val navController: NavController) : INavigationRouter {
    override fun getNavController(): NavController {
        return navController
    }

    override fun navigateToMapScreen() {
        navController.navigate(Destination.MapScreen.route)
    }

    override fun navigateToLoginScreen() {
        TODO("Not yet implemented")
    }

    override fun navigateToRegisterScreen() {
        navController.navigate(Destination.RegisterScreen.route)
    }
}