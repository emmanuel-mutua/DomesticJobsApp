package com.lorraine.hiremequick.presentation.jobseeker.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lorraine.hiremequick.data.model.JobPosting
import com.lorraine.hiremequick.presentation.jobseeker.moredetails.MoreDetailsScreen
import com.lorraine.hiremequick.presentation.jobseeker.moredetails.MoreDetailsViewModel
import com.lorraine.hiremequick.utils.Contants

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