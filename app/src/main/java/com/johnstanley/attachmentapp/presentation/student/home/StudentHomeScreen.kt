package com.johnstanley.attachmentapp.presentation.student.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
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
import androidx.navigation.compose.rememberNavController
import com.johnstanley.attachmentapp.presentation.auth.AuthViewModel
import com.johnstanley.attachmentapp.presentation.components.DisplayAlertDialog
import com.johnstanley.attachmentapp.presentation.student.add.AddLogScreen
import com.johnstanley.attachmentapp.presentation.student.navigation.BottomNavigationWithBackStack
import com.johnstanley.attachmentapp.presentation.student.navigation.StudentHomeDestinations
import com.johnstanley.attachmentapp.presentation.student.profile.ProfileScreen
import com.stevdzasan.messagebar.rememberMessageBarState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StudentHomeScreen(
    navigateToLogin: () -> Unit,
) {
    val messageBarState = rememberMessageBarState()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val viewModel: AuthViewModel = hiltViewModel()

    Scaffold(
        modifier = Modifier.fillMaxWidth()
            .statusBarsPadding().navigationBarsPadding(),
        bottomBar = {
            BottomNavigationWithBackStack(navController = navController)
        },
        content = {
            NavHost(
                navController = navController,
                startDestination = StudentHomeDestinations.Home.route,
            ) {
                homeContent(
                    drawerState = drawerState,
                    onMenuClicked = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onCalenderClicked = {},
                )
                add()
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
    drawerState: DrawerState,
    onMenuClicked: () -> Unit,
    onCalenderClicked: () -> Unit,
) {
    composable(route = StudentHomeDestinations.Home.route) {
        HomeContent(
            drawerState = drawerState,
            onMenuClicked = onMenuClicked,
            onCalenderClicked = onCalenderClicked,
        )
    }
}

fun NavGraphBuilder.add() {
    composable(route = StudentHomeDestinations.Add.route) {
        AddLogScreen()
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
