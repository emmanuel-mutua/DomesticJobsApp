package com.lorraine.domesticjobs.presentation.employer.home

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lorraine.domesticjobs.presentation.auth.AuthViewModel
import com.lorraine.domesticjobs.presentation.components.DisplayAlertDialog
import com.lorraine.domesticjobs.presentation.employer.add.AddJobScreen
import com.lorraine.domesticjobs.presentation.employer.add.AddLogViewModel
import com.lorraine.domesticjobs.presentation.employer.add.UpdateJobScreen
import com.lorraine.domesticjobs.presentation.employer.applications.ApplicationsHomeScreen
import com.lorraine.domesticjobs.presentation.employer.applications.JobApplicationsViewModel
import com.lorraine.domesticjobs.presentation.employer.navigation.BottomNavigationWithBackStack
import com.lorraine.domesticjobs.presentation.employer.navigation.EmployerHomeDestinations
import com.lorraine.domesticjobs.presentation.employer.profile.ProfileScreen
import com.lorraine.domesticjobs.presentation.employer.profile.ProfileViewModel
import com.lorraine.domesticjobs.presentation.jobseeker.navigation.JobSeekerBottomNavigationWithBackStack
import com.lorraine.domesticjobs.presentation.jobseeker.navigation.JobSeekerHomeDestinations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EmployerHomeScreen(
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
            if (currentRoute == EmployerHomeDestinations.Home.route ||
                currentRoute == EmployerHomeDestinations.Add.route ||
                currentRoute == EmployerHomeDestinations.Account.route
            ){
                BottomNavigationWithBackStack(navController = navController)
            }
        },
        content = {
            NavHost(
                navController = navController,
                startDestination = EmployerHomeDestinations.Home.route,
            ) {
                homeContent(
                    navigateToWrite = {
                        navController.navigate(EmployerHomeDestinations.Add.route)
                    },
                    navigateToApplications = { jobId ->
                        navController.navigate("${EmployerHomeDestinations.Notifications.route}/$jobId")
                    }
                )
                add(
                    onBackPressed = { navController.popBackStack() },
                )
                update(
                    onBackPressed = { navController.popBackStack() },
                )
                applications()
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
    navigateToWrite: () -> Unit,
    navigateToApplications: (String) -> Unit,
) {
    composable(route = EmployerHomeDestinations.Home.route) {
        val viewModel: EmployerHomeViewModel = hiltViewModel()
        val attachmentLogs = viewModel.jobPostings
        EmployerHomeScreen(
            jobs = attachmentLogs.value,
            navigateToWrite = navigateToWrite,
            navigateToApplications = navigateToApplications,
        )
    }
}

fun NavGraphBuilder.update(onBackPressed: () -> Unit) {
    composable(route = EmployerHomeDestinations.Update.route) {
        val viewModel: AddLogViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val context = LocalContext.current
        UpdateJobScreen(
            uiState = uiState,
            onTitleChanged = { viewModel.setTitle(title = it) },
            onDescriptionChanged = { viewModel.setDescription(description = it) },
            onDeleteConfirmed = {
                viewModel.deleteJobPosting(
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Deleted",
                            Toast.LENGTH_SHORT,
                        ).show()
                        // navigate back
                    },
                    onError = { message ->
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT,
                        ).show()
                    },
                )
            },
            onDateTimeUpdated = { viewModel.updateDateTime(zonedDateTime = it) },
            onBackPressed = onBackPressed,
            onSaveClicked = {
                viewModel.updateJob(
                    jobPosting = it,
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Success",
                            Toast.LENGTH_SHORT,
                        ).show()
                    },
                    onError = { message ->
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT,
                        ).show()
                    },
                )
            },
            onModeOfWorkChanged = {
                viewModel.setModeOfWork(it)
            },
            onNumberOfEmployeesUpdated = {
                viewModel.setNumberOfEmployeesNeeded(it)
            },
            applicationDeadlineUpdated = {
                viewModel.updateApplicationDeadlineDate(zonedDateTime = it)
            },
            jobStartingDateUpdated = {
                viewModel.updateJobStartingDate(zonedDateTime = it)
            },
            onNameOfCountryChanged = {
                viewModel.setNameOfCountry(it)
            },
            onSalaryUpdated = {
                viewModel.updateSalary(it)
            },
            onNameOfCityChanged = {
                viewModel.setNameOfCity(it)
            }
        )
    }
}

fun NavGraphBuilder.add(onBackPressed: () -> Unit) {
    composable(route = EmployerHomeDestinations.Add.route) {
        val viewModel: AddLogViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val context = LocalContext.current
        AddJobScreen(
            uiState = uiState,
            onTitleChanged = { viewModel.setTitle(title = it) },
            onDescriptionChanged = { viewModel.setDescription(description = it) },
            onDeleteConfirmed = {
                viewModel.deleteJobPosting(
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Deleted",
                            Toast.LENGTH_SHORT,
                        ).show()
                        // navigate back
                    },
                    onError = { message ->
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT,
                        ).show()
                    },
                )
            },
            onDateTimeUpdated = { viewModel.updateDateTime(zonedDateTime = it) },
            onBackPressed = onBackPressed,
            onSaveClicked = {
                viewModel.upsertJob(
                    jobPosting = it,
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Success",
                            Toast.LENGTH_SHORT,
                        ).show()
                    },
                    onError = { message ->
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT,
                        ).show()
                    },
                )
            },
            onModeOfWorkChanged = {
                viewModel.setModeOfWork(it)
            },
            onNumberOfEmployeesUpdated = {
                viewModel.setNumberOfEmployeesNeeded(it)
            },
            applicationDeadlineUpdated = {
                viewModel.updateApplicationDeadlineDate(zonedDateTime = it)
            },
            jobStartingDateUpdated = {
                viewModel.updateJobStartingDate(zonedDateTime = it)
            },
            onNameOfCityChanged = {
                viewModel.setNameOfCity(it)
            },
            onSalaryUpdated = {
                viewModel.updateSalary(it)
            },
            onNameOfCountryChanged = {
                viewModel.setNameOfCountry(it)
            }
        )
    }
}

fun NavGraphBuilder.account(
    scope: CoroutineScope,
    viewModel: AuthViewModel,
    navigateToLogin: () -> Unit,
) {
    composable(route = EmployerHomeDestinations.Account.route) {
        var dialogOpened by remember { mutableStateOf(false) }
        val profileViewModel: ProfileViewModel = hiltViewModel()
        val uiState by profileViewModel.employerUiState.collectAsState()
        ProfileScreen(
            onSignOutClicked = {
                dialogOpened = true
            },
            uiState = uiState
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

fun NavGraphBuilder.applications() {
    composable("${EmployerHomeDestinations.Notifications.route}/{jobId}") {
        val jobApplicationsViewModel: JobApplicationsViewModel = hiltViewModel()
        val jobId: String? = it.arguments?.getString("jobId")
        jobApplicationsViewModel.getApplications(jobId)
        val jobApplicationsUiState by jobApplicationsViewModel.uiState.collectAsState()
        val context = LocalContext.current
        ApplicationsHomeScreen(
            jobApplicationsUiState = jobApplicationsUiState,
            sendMessage = { phoneNumber ->
                jobApplicationsViewModel.sendMessage(phoneNumber = phoneNumber, context = context)
            },
            onApplicationEvent = { event ->
                jobApplicationsViewModel.onApplicationEvent(event)
            },
            declineJobSeeker = { applicantId, jobId ->
                jobApplicationsViewModel.declineJobSeeker(applicantId, jobId)
            },
            acceptJobSeeker = { applicantId, jobId ->
                jobApplicationsViewModel.acceptJobSeeker(applicantId, jobId)
            },
            downloadApplications = { applications ->
                jobApplicationsViewModel.downloadApplications(applications, context)
            }
            ,
            sendEmail = { emailAddress ->
                jobApplicationsViewModel.sendEmail(emailAddress = emailAddress, context = context)
            }
        ) { phoneNumber ->
            jobApplicationsViewModel.callApplicant(phoneNumber = phoneNumber, context = context)
        }
    }
}
