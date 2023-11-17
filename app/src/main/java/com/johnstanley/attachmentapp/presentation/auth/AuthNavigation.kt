package com.johnstanley.attachmentapp.presentation.auth

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.johnstanley.attachmentapp.presentation.staff.home.StaffHomeScreen
import com.johnstanley.attachmentapp.presentation.student.home.StudentHomeScreen
import com.johnstanley.attachmentapp.utils.Contants.StaffText
import com.johnstanley.attachmentapp.utils.Contants.StudentText
import com.stevdzasan.messagebar.rememberMessageBarState

@Composable
fun AuthNavGraph() {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = hiltViewModel()
    val isEmailVerified = viewModel.currentUser?.isEmailVerified ?: false
    val currentUser = viewModel.currentUser
    val startDestination = if (currentUser == null || !isEmailVerified) {
        AuthNavigation.Login.route
    } else {
        AuthNavigation.Home.route
    }
    val registerState = viewModel.registerState.collectAsState().value
    NavHost(navController = navController, startDestination = startDestination) {
        loginScreen(
            navController = navController,
            viewModel = viewModel,
            authStateData = registerState,
            navigateToRegister = {
                navController.navigateWithPop(AuthNavigation.Register.route)
            },
            navigateToHome = {
                navController.navigateWithPop(AuthNavigation.Home.route)
            },
        )
        registerScreen(viewModel = viewModel, navigateToLogin = {
            navController.navigateWithPop(AuthNavigation.Login.route)
        })
        homeScreen(viewModel = viewModel, navigateToLogin = {
            navController.navigateWithPop(AuthNavigation.Login.route)
        })
    }
}

fun NavGraphBuilder.loginScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    authStateData: AuthStateData,
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit,
) {
    composable(AuthNavigation.Login.route) {
        LoginScreen(
            navController = navController,
            viewModel = viewModel,
            authStateData = authStateData,
            onRegisterButtonClicked = navigateToRegister,
            navigateToHome = navigateToHome,
        )
    }
}

fun NavGraphBuilder.registerScreen(
    viewModel: AuthViewModel,
    navigateToLogin: () -> Unit,
) {
    composable(AuthNavigation.Register.route) {
        val messageBarState = rememberMessageBarState()
        RegisterScreen(
            viewModel = viewModel,
            messageBarState = messageBarState,
            onSuccessRegistration = {
                viewModel.sendEmailVerification()
                navigateToLogin()
            },
            onGotoLoginClicked = {
                navigateToLogin()
            },
        )
    }
}

fun NavGraphBuilder.homeScreen(viewModel: AuthViewModel, navigateToLogin: () -> Unit) {
    composable(AuthNavigation.Home.route) {
        val role = viewModel.registerState.collectAsState().value.role
        Log.d("Login", role)
        if (role == StudentText) {
            StudentHomeScreen()
        } else if (role == StaffText) {
            StaffHomeScreen()
        } else {
            navigateToLogin()
        }
    }
}

fun NavController.navigateWithPop(route: String) {
    this.navigate(route) {
        popUpTo(AuthNavigation.Login.route) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

sealed class AuthNavigation(val route: String) {
    object Login : AuthNavigation(route = "login")
    object Register : AuthNavigation(route = "register")
    object Home : AuthNavigation(route = "home")
}
