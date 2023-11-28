package com.johnstanley.attachmentapp.presentation.student.home

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.rememberPagerState
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
import com.johnstanley.attachmentapp.presentation.auth.AuthViewModel
import com.johnstanley.attachmentapp.presentation.components.DisplayAlertDialog
import com.johnstanley.attachmentapp.presentation.student.add.AddLogScreen
import com.johnstanley.attachmentapp.presentation.student.add.AddLogViewModel
import com.johnstanley.attachmentapp.presentation.student.add.UpdateLogScreen
import com.johnstanley.attachmentapp.presentation.student.navigation.BottomNavigationWithBackStack
import com.johnstanley.attachmentapp.presentation.student.navigation.StudentHomeDestinations
import com.johnstanley.attachmentapp.presentation.student.profile.ProfileScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StudentHomeScreen(
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
            if (currentRoute != StudentHomeDestinations.Add.route) {
                BottomNavigationWithBackStack(navController = navController)
            }
        },
        content = {
            NavHost(
                navController = navController,
                startDestination = StudentHomeDestinations.Home.route,
            ) {
                homeContent(
                    navigateToWriteWithArgs = {
                        navController.navigate(
                            StudentHomeDestinations.Update.passAttachLogId(
                                attachLogId = it,
                            ),
                        )
                    },
                    navigateToWrite = {
                        navController.navigate(StudentHomeDestinations.Add.route)
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
    composable(route = StudentHomeDestinations.Home.route) {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val viewModel: StudentHomeViewModel = hiltViewModel()
        val attachmentLogs = viewModel.attachmentLogs
        val scope = rememberCoroutineScope()
        StudentHomeScreen(
            attachmentLogs = attachmentLogs.value,
            drawerState = drawerState,
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                }
            },
            dateIsSelected = viewModel.dateIsSelected,
            onDateSelected = { viewModel.getAttachmentLogs(zonedDateTime = it) },
            onDateReset = { viewModel.getAttachmentLogs() },
            navigateToWriteWithArgs = navigateToWriteWithArgs,
            navigateToWrite = navigateToWrite,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun NavGraphBuilder.update(onBackPressed: () -> Unit) {
    composable(route = StudentHomeDestinations.Update.route) {
        val viewModel: AddLogViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val context = LocalContext.current
        val galleryState = viewModel.galleryState
        val pagerState = rememberPagerState(pageCount = { 5 })
        UpdateLogScreen(
            uiState = uiState,
            pagerState = pagerState,
            galleryState = galleryState,
            onTitleChanged = { viewModel.setTitle(title = it) },
            onDescriptionChanged = { viewModel.setDescription(description = it) },
            onDeleteConfirmed = {
                viewModel.deleteAttachmentLog(
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
                viewModel.updateLog(
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
            onImageSelect = {
                val type = context.contentResolver.getType(it)?.split("/")?.last() ?: "jpg"
                viewModel.addImage(image = it, imageType = type)
            },
            onImageDeleteClicked = {
                galleryState.removeImage(it)
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun NavGraphBuilder.add(onBackPressed: () -> Unit) {
    composable(route = StudentHomeDestinations.Add.route) {
        val viewModel: AddLogViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val context = LocalContext.current
        val galleryState = viewModel.galleryState
        val pagerState = rememberPagerState(pageCount = { 5 })
        AddLogScreen(
            uiState = uiState,
            pagerState = pagerState,
            galleryState = galleryState,
            onTitleChanged = { viewModel.setTitle(title = it) },
            onDescriptionChanged = { viewModel.setDescription(description = it) },
            onDeleteConfirmed = {
                viewModel.deleteAttachmentLog(
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
                viewModel.upsertLog(
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
            onImageSelect = {
                val type = context.contentResolver.getType(it)?.split("/")?.last() ?: "jpg"
                viewModel.addImage(image = it, imageType = type)
            },
            onImageDeleteClicked = {
                galleryState.removeImage(it)
            },
        )
    }
}

fun NavGraphBuilder.account(
    scope: CoroutineScope,
    viewModel: AuthViewModel,
    navigateToLogin: () -> Unit,
) {
    composable(route = StudentHomeDestinations.Account.route) {
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
    composable(StudentHomeDestinations.Notifications.route) {
        TestScreen(text = "Notifications")
    }
}
