package com.lorraine.hiremequick.presentation.auth

sealed class AuthScreen(val route: String) {
    data object Welcome : AuthScreen(route = "welcome")
    data object Login : AuthScreen(route = "login")
    data object Register : AuthScreen(route = "register")
    data object Home : AuthScreen(route = "home")
    data object JobSeekerHome : AuthScreen(route = "student")
    data object EmployerHome : AuthScreen(route = "staff")
}
