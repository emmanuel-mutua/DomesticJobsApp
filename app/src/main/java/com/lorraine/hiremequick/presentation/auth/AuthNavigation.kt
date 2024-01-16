package com.lorraine.hiremequick.presentation.auth

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lorraine.hiremequick.presentation.staff.home.JobSeekerHomeScreen
import com.lorraine.hiremequick.presentation.employer.home.EmployerHomeScreen
import com.lorraine.hiremequick.utils.Contants.Employer
import com.lorraine.hiremequick.utils.Contants.JobSeeker

@Composable
fun AuthNavGraph(
    startDestination: String,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    registerState: AuthStateData,
    activity: Activity,
) {
    NavHost(navController = navController, startDestination = startDestination) {
        welcomeScreen(
            onGetStartedClick = {
                navController.navigateWithPop(AuthScreen.Register.route)
            },
            onLoginButtonClick = {
                navController.navigateWithPop(AuthScreen.Login.route)
            }
        )
        loginScreen(
            registerState = registerState,
            viewModel = authViewModel,
            navigateBack = {
                navController.popBackStack()
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
        registerScreen(
            viewModel = authViewModel,
            navigateToLogin = {
                navController.navigateWithPop(AuthScreen.Login.route)
            },
            navigateBack = {
                navController.popBackStack()
            }
        )
        homeScreen(
            registerState = registerState,
            viewModel = authViewModel,
            navigateToLogin = {
                navController.popBackStack()
                navController.navigate(AuthScreen.Login.route)
            },
            navigateToJobSeeker = {
                navController.popBackStack()
                navController.navigate(AuthScreen.JobSeekerHome.route)
            },
            navigateToEmployer = {
                navController.popBackStack()
                navController.navigate(AuthScreen.EmployerHome.route)
            },

            )
        jobSeekerHomeScreen(
            navigateToLogin = {
                activity.finish()
            },
        )
        employerHomeScreen(
            navigateToLogin = {
                activity.finish()
            }
        )
    }
}

fun NavGraphBuilder.loginScreen(
    registerState: AuthStateData,
    viewModel: AuthViewModel,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
) {
    composable(AuthScreen.Login.route) {
        LoginScreen(
            registerState = registerState,
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToHome = navigateToHome,
        )
    }
}

fun NavGraphBuilder.registerScreen(
    viewModel: AuthViewModel,
    navigateToLogin: () -> Unit,
    navigateBack: () -> Unit,
) {
    composable(AuthScreen.Register.route) {
        RegisterScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
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
    navigateToEmployer: () -> Unit,
    navigateToJobSeeker: () -> Unit,
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
                        JobSeeker -> navigateToJobSeeker()
                        Employer -> navigateToEmployer()
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

fun NavGraphBuilder.welcomeScreen(
    onGetStartedClick: () -> Unit,
    onLoginButtonClick: () -> Unit,
) {
    composable(AuthScreen.Welcome.route) {
        WelcomeScreen(
            onGetStartedClick = onGetStartedClick,
            onLoginButtonClick = onLoginButtonClick
        )
    }
}

fun NavGraphBuilder.jobSeekerHomeScreen(navigateToLogin: () -> Unit) {
    composable(AuthScreen.JobSeekerHome.route) {
        JobSeekerHomeScreen()
    }
}

fun NavGraphBuilder.employerHomeScreen(navigateToLogin: () -> Unit) {
    composable(AuthScreen.EmployerHome.route) {
        EmployerHomeScreen(
            navigateToLogin = navigateToLogin,
        )
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
