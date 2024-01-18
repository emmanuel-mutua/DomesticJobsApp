package com.lorraine.hiremequick.presentation.jobseeker.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lorraine.hiremequick.data.model.JobPosting
import com.lorraine.hiremequick.presentation.auth.AuthViewModel
import com.lorraine.hiremequick.presentation.components.DisplayAlertDialog
import com.lorraine.hiremequick.presentation.employer.navigation.EmployerHomeDestinations
import com.lorraine.hiremequick.presentation.employer.profile.ProfileScreen
import com.lorraine.hiremequick.presentation.jobseeker.home.JobSeekerHomeScreen
import com.lorraine.hiremequick.presentation.jobseeker.home.JobSeekerHomeViewModel
import com.lorraine.hiremequick.presentation.jobseeker.home.TestScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun JobSeekerHomeDashboard(
    navigateToLogin: () -> Unit,
) {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .navigationBarsPadding(),
        bottomBar = {
            val currentRoute =
                navController.currentBackStackEntryAsState()?.value?.destination?.route
            if (currentRoute != JobSeekerHomeDestinations.MoreDetailsScreen.route){
                JobSeekerBottomNavigationWithBackStack(navController = navController)
            }
        },
        content = {
            NavHost(
                navController = navController,
                startDestination = JobSeekerHomeDestinations.Home.route,
            ) {
                homeContent(
                    onJobClick = {
                        //save job posting to state
                        navController.navigate(JobSeekerHomeDestinations.MoreDetailsScreen.passJobPosting(it))
                    }
                )
                moreDetailsScreen()
                jobApplications()
                account(
                    scope = scope,
                    navigateToLogin = navigateToLogin,
                    viewModel = viewModel,
                )
            }
        },
    )
}

fun NavGraphBuilder.homeContent(
    onJobClick: (JobPosting) -> Unit
) {
    composable(route = EmployerHomeDestinations.Home.route) {
        val viewModel: JobSeekerHomeViewModel = hiltViewModel()
        val jobPostings = viewModel.jobPostings
        JobSeekerHomeScreen(
            jobPostings = jobPostings.value,
            onJobClick = onJobClick
        )
    }
}
fun NavGraphBuilder.moreDetailsScreen() {
    composable(JobSeekerHomeDestinations.MoreDetailsScreen.route) {
        MoreDetailsNavHost()
    }
}
fun NavGraphBuilder.jobApplications() {
    composable(JobSeekerHomeDestinations.JobApplications.route) {
        TestScreen(text = "Job Applications")
    }
}


fun NavGraphBuilder.account(
    scope: CoroutineScope,
    viewModel: AuthViewModel,
    navigateToLogin: () -> Unit,
) {
    composable(route = JobSeekerHomeDestinations.Account.route) {
        var dialogOpened by remember { mutableStateOf(false) }
        ProfileScreen(
            onSignOutClicked = {
                dialogOpened = true
            },
        )
        DisplayAlertDialog(
            title = "Sign Out",
            message = "Are you sure you want to sign out?",
            dialogOpened = dialogOpened,
            onCloseDialog = {
                dialogOpened = false
            },
            onYesClicked = {
                scope.launch {
                    viewModel.signOut()
                    navigateToLogin()
                }
            },
        )
    }
}

