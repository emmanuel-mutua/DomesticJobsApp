package com.lorraine.domesticjobs.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lorraine.domesticjobs.presentation.employer.home.EmployerHomeScreen
import com.lorraine.domesticjobs.presentation.jobseeker.navigation.JobSeekerHomeDashboard
import com.lorraine.domesticjobs.utils.Contants.Employer
import com.lorraine.domesticjobs.utils.Contants.JobSeeker

@Composable
fun AuthNavGraph(
    startDestination: String,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    registerState: AuthStateData,
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
            }
        ) {
            navController.navigate(AuthScreen.ForgotPassword.route)
        }
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
                navController.navigateAndPopHome(AuthScreen.Welcome.route)
            },
        )
        employerHomeScreen(
            navigateToLogin = {
                navController.navigateAndPopHome(AuthScreen.Welcome.route)
            }
        )
        forgotPasswordScreen(
            navigateToLogin = {
                navController.popBackStack()
            }
        ) { email ->
            authViewModel.resetPassword(email)
        }
    }
}

fun NavGraphBuilder.loginScreen(
    viewModel: AuthViewModel,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToForgotPassword: () -> Unit,
) {
    composable(AuthScreen.Login.route) {
        LoginScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToHome = navigateToHome,
            navigateToForgotPassword = navigateToForgotPassword
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
                navigateToLogin()
            },
            onGotoLoginClicked = {
                navigateToLogin()
            },
        )
    }
}
fun NavGraphBuilder.forgotPasswordScreen(
    navigateToLogin: () -> Unit,
    resetPassword: (String) -> Boolean,
    ){
    composable(AuthScreen.ForgotPassword.route){
        ForgotPasswordScreen(
            navigateBack = navigateToLogin,
            resetPassword = resetPassword
        )
    }
}
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
        JobSeekerHomeDashboard(
            navigateToLogin = navigateToLogin
        )
    }
}

fun NavGraphBuilder.employerHomeScreen(navigateToLogin: () -> Unit) {
    composable(AuthScreen.EmployerHome.route) {
        EmployerHomeScreen(
            navigateToLogin = navigateToLogin,
        )
    }
}

private fun NavController.navigateWithPop(route: String) {
    navigate(route) {
        popUpTo(AuthScreen.Welcome.route) {
            saveState = true
        }
        restoreState = true
    }
}

private fun NavController.navigateAndPopHome(route:String){
    popBackStack()
    navigate(route){
        popUpTo(AuthScreen.Welcome.route)
    }
}

