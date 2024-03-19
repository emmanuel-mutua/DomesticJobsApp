package com.lorraine.domesticjobs.presentation.jobseeker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lorraine.domesticjobs.data.model.JobPosting
import com.lorraine.domesticjobs.presentation.jobseeker.moredetails.MoreDetailsScreen

sealed class MoreDetailsNavigation(
    val route: String
) {
    object MoreDetailsScreen : MoreDetailsNavigation(
        route = "moredetailhome",
    )
}

@Composable
fun MoreDetailsNavHost(jobPosting: JobPosting, navigateBack: () -> Unit) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MoreDetailsNavigation.MoreDetailsScreen.route,
    ) {
        composable(
            MoreDetailsNavigation.MoreDetailsScreen.route
        ) {
            MoreDetailsScreen(
                jobPosting = jobPosting,
                navigateBack = navigateBack,
            )
        }
    }
}