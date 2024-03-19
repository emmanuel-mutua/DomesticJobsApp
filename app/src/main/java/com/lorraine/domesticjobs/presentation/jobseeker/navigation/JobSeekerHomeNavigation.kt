package com.lorraine.domesticjobs.presentation.jobseeker.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.lorraine.domesticjobs.presentation.auth.AuthViewModel
import com.lorraine.domesticjobs.presentation.components.DisplayAlertDialog
import com.lorraine.domesticjobs.presentation.employer.navigation.EmployerHomeDestinations
import com.lorraine.domesticjobs.presentation.jobseeker.applications.JobSeekerApplicationsHomeScreen
import com.lorraine.domesticjobs.presentation.jobseeker.applications.MyJobApplicationsViewModel
import com.lorraine.domesticjobs.presentation.jobseeker.home.JobSeekerHomeScreen
import com.lorraine.domesticjobs.presentation.jobseeker.home.JobSeekerHomeViewModel
import com.lorraine.domesticjobs.presentation.jobseeker.profile.ProfileScreen
import com.lorraine.domesticjobs.presentation.jobseeker.profile.ProfileViewModel
import com.lorraine.domesticjobs.presentation.jobseeker.moredetails.MoreDetailsViewModel
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
                navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute == JobSeekerHomeDestinations.Home.route ||
            currentRoute == JobSeekerHomeDestinations.JobApplications.route ||
            currentRoute == JobSeekerHomeDestinations.Account.route
                ){
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
                        navController.navigate("${JobSeekerHomeDestinations.MoreDetailsScreen.route}/$it")
                    }
                )
                moreDetailsScreen(
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
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
    onJobClick: (String) -> Unit
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
fun NavGraphBuilder.moreDetailsScreen(
    navigateBack : () -> Unit
) {
    composable("${JobSeekerHomeDestinations.MoreDetailsScreen.route}/{jobPost}") {
        val jobPostingId : String? = it.arguments?.getString("jobPost")
        val moreDetailsVm : MoreDetailsViewModel = hiltViewModel()
        moreDetailsVm.getSelectedJob(jobPostingId)
        val jobPosting = moreDetailsVm.uiState.collectAsState().value.selectedJob
        MoreDetailsNavHost(jobPosting = jobPosting, navigateBack = navigateBack)
    }
}
fun NavGraphBuilder.jobApplications() {
    composable(JobSeekerHomeDestinations.JobApplications.route) {
        val vm : MyJobApplicationsViewModel = hiltViewModel()
        val uiState by vm.uiState.collectAsState()
        JobSeekerApplicationsHomeScreen(
            jobApplicationsUiState = uiState
        )
    }
}


fun NavGraphBuilder.account(
    scope: CoroutineScope,
    viewModel: AuthViewModel,
    navigateToLogin: () -> Unit,
) {
    composable(route = JobSeekerHomeDestinations.Account.route) {
        var dialogOpened by remember { mutableStateOf(false) }
        val profileViewModel: ProfileViewModel = hiltViewModel()
        val uiState by profileViewModel.jobSeekerUiState.collectAsState()
        ProfileScreen(
            uiState = uiState,
            onSignOutClicked = {
                dialogOpened = true
            }
        )
        DisplayAlertDialog(
            title = "Sign Out",
            message = "Are you sure you want to sign out and exit?",
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

