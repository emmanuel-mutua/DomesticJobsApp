package com.johnstanley.attachmentapp.presentation.auth

sealed class AuthScreen(val route: String) {
    object Login : AuthScreen(route = "login")
    object Register : AuthScreen(route = "register")
    object Home : AuthScreen(route = "home")
    object StudentHome : AuthScreen(route = "student")
    object StaffHome : AuthScreen(route = "staff")
}
