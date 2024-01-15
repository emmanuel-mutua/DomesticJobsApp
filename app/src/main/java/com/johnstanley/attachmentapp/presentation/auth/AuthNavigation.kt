package com.johnstanley.attachmentapp.presentation.auth

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.johnstanley.attachmentapp.presentation.staff.home.StaffHomeScreen
import com.johnstanley.attachmentapp.presentation.student.home.StudentHomeScreen
import com.johnstanley.attachmentapp.utils.Contants.StaffText
import com.johnstanley.attachmentapp.utils.Contants.StudentText
import com.stevdzasan.messagebar.rememberMessageBarState

@Composable
fun AuthNavGraph(
    startDestination: String,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    registerState: AuthStateData,
    activity: Activity,
) {
    NavHost(navController = navController, startDestination = startDestination) {
        loginScreen(
            registerState = registerState,
            viewModel = authViewModel,
            navigateToRegister = {
               navController.navigateWithPop(AuthScreen.Register.route)
            },
            navigateToHome = {
                navController.navigate(AuthScreen.Home.route) {
                    navController.popBackStack()
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            },
        )
        registerScreen(viewModel = authViewModel, navigateToLogin = {
            navController.navigateWithPop(AuthScreen.Login.route)
        })
        homeScreen(
            registerState = registerState,
            viewModel = authViewModel,
            navigateToLogin = {
                navController.popBackStack()
                navController.navigate(AuthScreen.Login.route)
            },
            navigateToStudent = {
                navController.popBackStack()
                navController.navigate(AuthScreen.StudentHome.route)
            },
            navigateToStaff = {
                navController.popBackStack()
                navController.navigate(AuthScreen.StaffHome.route)
            },

        )
        studentHomeScreen(
            navigateToLogin = {
                activity.finish()

//                navController.popBackStack()
//                navController.navigate(AuthScreen.Login.route)
            },
        )
        staffHomeScreen()
    }
}

fun NavGraphBuilder.loginScreen(
    registerState: AuthStateData,
    viewModel: AuthViewModel,
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit,
) {
    composable(AuthScreen.Login.route) {
        LoginScreen(
            registerState = registerState,
            viewModel = viewModel,
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
    registerState: AuthStateData,
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

            if (registerState.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}

fun NavGraphBuilder.studentHomeScreen(navigateToLogin: () -> Unit) {
    composable(AuthScreen.StudentHome.route) {
        StudentHomeScreen(
            navigateToLogin = navigateToLogin,
        )
    }
}

fun NavGraphBuilder.staffHomeScreen() {
    composable(AuthScreen.StaffHome.route) {
        StaffHomeScreen()
    }
}

fun NavController.navigateWithPop(route: String) {
    navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
