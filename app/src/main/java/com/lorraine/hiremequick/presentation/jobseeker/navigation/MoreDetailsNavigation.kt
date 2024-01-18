package com.lorraine.hiremequick.presentation.jobseeker.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lorraine.hiremequick.data.model.JobPosting
import com.lorraine.hiremequick.presentation.jobseeker.home.TestScreen
import com.lorraine.hiremequick.presentation.jobseeker.moredetails.ApplyJobScreen
import com.lorraine.hiremequick.presentation.jobseeker.moredetails.MoreDetailsScreen
import com.lorraine.hiremequick.presentation.jobseeker.moredetails.MoreDetailsViewModel
import com.lorraine.hiremequick.utils.Contants

sealed class MoreDetailsNavigation(
    val route: String
) {
    object MoreDetailsScreen : MoreDetailsNavigation(
        route = "moredetailhome",
    )

    object Apply : MoreDetailsNavigation(
        route = "apply",
    )
}

@Composable
fun MoreDetailsNavHost(jobPosting: JobPosting, navigateBack: () ->Unit) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MoreDetailsNavigation.MoreDetailsScreen.route,
    ) {
        composable(
            MoreDetailsNavigation.MoreDetailsScreen.route
        ) {
            MoreDetailsScreen(jobPosting = jobPosting, navigateBack = navigateBack)
        }
        composable(MoreDetailsNavigation.Apply.route) {
            ApplyJobScreen()
        }
    }
}