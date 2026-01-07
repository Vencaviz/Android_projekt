package com.example.homework2.navigation

import androidx.navigation.NavController

interface INavigationRouter {
    fun getNavController(): NavController
    fun navigateToMapScreen()
    fun navigateToLoginScreen()
    fun navigateToRegisterScreen()
}


