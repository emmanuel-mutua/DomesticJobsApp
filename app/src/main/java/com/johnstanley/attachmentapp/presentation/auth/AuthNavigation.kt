package com.johnstanley.attachmentapp.presentation.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AuthNavGraph() {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = hiltViewModel()
    val startDestination =
        if (viewModel.currentUser != null && viewModel.isEmailVerified) {
            AuthScreen.Home.route
        } else {
            AuthScreen.Login.route
        }
    val registerState = viewModel.registerState.collectAsState().value
    NavHost(navController = navController, startDestination = startDestination) {
        loginScreen(
            navController = navController,
            viewModel = viewModel,
            authStateData = registerState,
            navigateToRegister = {
                navController.navigate(AuthScreen.Register.route)
            },
            navigateToHome = {
                navController.navigate(AuthScreen.Home.route)
            },
        )
        registerScreen(viewModel = viewModel, navigateToLogin = {
            navController.popBackStack()
        })
        homeScreen(
            viewModel = viewModel,
            navigateToLogin = {
                navController.navigate(AuthScreen.Login.route)
            },
            navigateToStudent = {
                navController.navigate(AuthScreen.StudentHome.route)
            },
            navigateToStaff = {
                navController.navigate(AuthScreen.StaffHome.route)
            },

        )
        studentHomeScreen(
            navigateToLogin = {
                navController.navigate(AuthScreen.Login.route)
            }
        )
        staffHomeScreen()
    }
}

fun NavGraphBuilder.loginScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    authStateData: AuthStateData,
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit,
) {
    composable(AuthScreen.Login.route) {
        LoginScreen(
            navController = navController,
            viewModel = viewModel,
            authStateData = authStateData,
            navigateToRegister = navigateToRegister,
            navigateToHome = navigateToHome,
        )
    }
}

fun NavGraphBuilder.registerScreen(
    viewModel: AuthViewModel,
    navigateToLogin: () -> Unit,
) {
    composable(AuthScreen.Register.route) {
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

@SuppressLint("CoroutineCreationDuringComposition")
fun NavGraphBuilder.homeScreen(
    viewModel: AuthViewModel,
    navigateToLogin: () -> Unit,
    navigateToStaff: () -> Unit,
    navigateToStudent: () -> Unit,
) {
    composable(AuthScreen.Home.route) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val registerState = viewModel.registerState.collectAsState().value
            val coroutineScope = rememberCoroutineScope()
            coroutineScope.launch(Dispatchers.IO) {
                viewModel.getRoleFromUserData { role ->
                    if (role.isNotEmpty()) {
                        when (role) {
                            StudentText -> navigateToStudent()
                            StaffText -> navigateToStaff()
                            else -> navigateToLogin()
                        }
                    } else {
                        navigateToLogin()
                    }
                }
            }
            if (registerState.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}

fun NavGraphBuilder.studentHomeScreen(navigateToLogin: () -> Unit) {
    composable(AuthScreen.StudentHome.route) {
        StudentHomeScreen(
           navigateToLogin =  navigateToLogin
        )
    }
}

fun NavGraphBuilder.staffHomeScreen() {
    composable(AuthScreen.StaffHome.route) {
        StaffHomeScreen()
    }
}

fun NavController.navigateWithPop(route: String) {
    navigate(route)
}
