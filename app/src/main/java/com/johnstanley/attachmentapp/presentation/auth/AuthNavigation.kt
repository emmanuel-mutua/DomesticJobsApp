package com.johnstanley.attachmentapp.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.johnstanley.attachmentapp.presentation.staff.home.StaffHomeScreen
import com.johnstanley.attachmentapp.presentation.student.home.StudentHomeScreen

@Composable
fun AuthNavGraph() {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = hiltViewModel()
    val startDestination = if (viewModel.currentUser == null) {
        AuthNavigation.Login.route
    } else {
        AuthNavigation.Home.route
    }
    val userData = viewModel.registerState.collectAsState().value
    NavHost(navController = navController, startDestination = startDestination) {
        loginScreen(
            viewModel = viewModel,
            userData = userData,
            navigateToRegister = {
                navController.navigate(AuthNavigation.Register.route)
            },
            navigateToHome = {
                navController.navigate(AuthNavigation.Home.route)
            },
        )
        registerScreen(userData = userData, viewModel = viewModel, navigateToLogin = {
            navController.navigate(AuthNavigation.Login.route)
        })
        homeScreen(isStudentLoggedIn = userData.goToStudentHomeScreen)
    }
}

fun NavGraphBuilder.loginScreen(
    viewModel: AuthViewModel,
    userData: UserData,
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit,
) {
    composable(AuthNavigation.Login.route) {
        LoginScreen(
            viewModel = viewModel,
            userData = userData,
            navigateToRegister = navigateToRegister,
            navigateToHome = navigateToHome,
        )
    }
}

fun NavGraphBuilder.registerScreen(
    userData: UserData,
    viewModel: AuthViewModel,
    navigateToLogin: () -> Unit,
) {
    composable(AuthNavigation.Register.route) {
        RegisterScreen(
            userData = userData,
            viewModel = viewModel,
            navigateToLogin = navigateToLogin,
        )
    }
}

fun NavGraphBuilder.homeScreen(isStudentLoggedIn: Boolean) {
    composable(AuthNavigation.Home.route) {
        if (isStudentLoggedIn) {
            StudentHomeScreen()
        } else {
            StaffHomeScreen()
        }
    }
}

sealed class AuthNavigation(val route: String) {
    object Login : AuthNavigation(route = "login")
    object Register : AuthNavigation(route = "register")
    object Home : AuthNavigation(route = "home")
}
