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
        modifier = Modifier.fillMaxWidth()
            .statusBarsPadding().navigationBarsPadding(),
        bottomBar = {
            val currentRoute =
                navController.currentBackStackEntryAsState()?.value?.destination?.route
            if (currentRoute != EmployerHomeDestinations.Add.route) {
                BottomNavigationWithBackStack(navController = navController)
            }
        },
        content = {
            NavHost(
                navController = navController,
                startDestination = EmployerHomeDestinations.Home.route,
            ) {
                homeContent(
                    navigateToWriteWithArgs = {
                        navController.navigate(
                            EmployerHomeDestinations.Update.passJobPostingId(
                                attachLogId = it,
                            ),
                        )
                    },
                    navigateToWrite = {
                        navController.navigate(EmployerHomeDestinations.Add.route)
                    },
                )
                add(
                    onBackPressed = { navController.popBackStack() },
                )
                update(
                    onBackPressed = { navController.popBackStack() },
                )
                notifications()
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
    navigateToWriteWithArgs: (String) -> Unit,
    navigateToWrite: () -> Unit,
) {
    composable(route = EmployerHomeDestinations.Home.route) {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val viewModel: JobSeekerHomeViewModel = hiltViewModel()
        val attachmentLogs = viewModel.jobPostings
        val scope = rememberCoroutineScope()
        JobSeekerHomeScreen(
            jobPostings = attachmentLogs.value,
            drawerState = drawerState,
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                }
            },
            navigateToWriteWithArgs = navigateToWriteWithArgs,
            navigateToWrite = navigateToWrite,
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
            onNameOfCountryChanged = {
                viewModel.setNameOfCountry(it)
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
                    attachmentLog = it,
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
            onNameOfCityChanged = {
                viewModel.setNameOfCity(it)
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

fun NavGraphBuilder.notifications() {
    composable(EmployerHomeDestinations.Notifications.route) {
    }
}
