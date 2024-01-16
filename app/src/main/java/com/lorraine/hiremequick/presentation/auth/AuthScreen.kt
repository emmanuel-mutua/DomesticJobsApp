package com.lorraine.hiremequick.presentation.auth

sealed class AuthScreen(val route: String) {
    object Welcome : AuthScreen(route = "welcome")
    object Login : AuthScreen(route = "login")
    object Register : AuthScreen(route = "register")
    object Home : AuthScreen(route = "home")
    object JobSeekerHome : AuthScreen(route = "student")
    object EmployerHome : AuthScreen(route = "staff")
}
