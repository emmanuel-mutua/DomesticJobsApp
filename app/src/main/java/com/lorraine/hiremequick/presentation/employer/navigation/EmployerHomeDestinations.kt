package com.lorraine.hiremequick.presentation.employer.navigation

import com.lorraine.hiremequick.R
import com.lorraine.hiremequick.utils.Contants.ADD_SCREEN_ARGUMENT_KEY

sealed class EmployerHomeDestinations(
    val route: String,
    val label: String,
    val icon: Int,
) {
    object Home : EmployerHomeDestinations(
        route = "home",
        label = "Home",
        icon = R.drawable.baseline_home,
    )

    object Add : EmployerHomeDestinations(
        route = "add_job",
        label = "Add Job",
        icon = R.drawable.baseline_add,
    )

    object Update : EmployerHomeDestinations(
        route = "update_job?$ADD_SCREEN_ARGUMENT_KEY=" + "{$ADD_SCREEN_ARGUMENT_KEY}",
        label = "Update Job",
        icon = R.drawable.baseline_add,
    ) {
        fun passJobPostingId(attachLogId: String) =
            "update_job?$ADD_SCREEN_ARGUMENT_KEY=$attachLogId"
    }

    object Notifications : EmployerHomeDestinations(
        route = "applications",
        label = "Applications",
        icon = R.drawable.baseline_notifications_active_24,
    )

    object Account : EmployerHomeDestinations(
        route = "account",
        label = "Account",
        icon = R.drawable.baseline_person,
    )
}
