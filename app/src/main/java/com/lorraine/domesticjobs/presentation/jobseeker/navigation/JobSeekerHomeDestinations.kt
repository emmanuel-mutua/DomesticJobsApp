package com.lorraine.domesticjobs.presentation.jobseeker.navigation

import com.lorraine.domesticjobs.R

sealed class JobSeekerHomeDestinations(
    val route: String,
    val label: String,
    val icon: Int?,
) {
    object Home : JobSeekerHomeDestinations(
        route = "home",
        label = "Home",
        icon = R.drawable.baseline_home,
    )
    object MoreDetailsScreen : JobSeekerHomeDestinations(
        route = "moredetails",
        label = "MoreDetails",
        icon = null,
    )

    object JobApplications : JobSeekerHomeDestinations(
        route = "JobApplications",
        label = "Applications",
        icon = R.drawable.baseline_notifications_active_24,
    )

    object Account : JobSeekerHomeDestinations(
        route = "account",
        label = "Account",
        icon = R.drawable.baseline_person,
    )
}
