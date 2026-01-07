package com.example.homework2.navigation

sealed class Destination(val route: String) {

    object MapScreen : Destination(route = "map")
    object LoginScreen: Destination(route = "login")
    object RegisterScreen: Destination(route = "register")
}