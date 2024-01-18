package com.lorraine.hiremequick.presentation.jobseeker.home

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lorraine.hiremequick.presentation.auth.AuthViewModel
import com.lorraine.hiremequick.presentation.components.DisplayAlertDialog
import com.lorraine.hiremequick.presentation.employer.add.AddJobScreen
import com.lorraine.hiremequick.presentation.employer.add.AddLogViewModel
import com.lorraine.hiremequick.presentation.employer.add.UpdateJobScreen
import com.lorraine.hiremequick.presentation.employer.navigation.BottomNavigationWithBackStack
import com.lorraine.hiremequick.presentation.employer.navigation.EmployerHomeDestinations
import com.lorraine.hiremequick.presentation.employer.profile.ProfileScreen
import com.lorraine.hiremequick.presentation.jobseeker.navigation.JobSeekerBottomNavigationWithBackStack
import com.lorraine.hiremequick.presentation.jobseeker.navigation.JobSeekerHomeDestinations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun JobSeekerHomeScreen(
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
            JobSeekerBottomNavigationWithBackStack(navController = navController)

        },
        content = {
            NavHost(
                navController = navController,
                startDestination = JobSeekerHomeDestinations.Home.route,
            ) {
                homeContent()
                searchScreen()
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

fun NavGraphBuilder.homeContent() {
    composable(route = EmployerHomeDestinations.Home.route) {
        val viewModel: JobSeekerHomeViewModel = hiltViewModel()
        val jobPostings = viewModel.jobPostings
        JobSeekerHomeScreen(
            jobPostings = jobPostings.value,
        )
    }
}
fun NavGraphBuilder.searchScreen() {
    composable(JobSeekerHomeDestinations.Search.route) {
        TestScreen(text = "Search")
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

