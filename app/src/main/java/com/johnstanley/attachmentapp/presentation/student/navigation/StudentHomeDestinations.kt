package com.johnstanley.attachmentapp.presentation.student.navigation

import com.johnstanley.attachmentapp.R
import com.johnstanley.attachmentapp.utils.Contants.ADD_SCREEN_ARGUMENT_KEY

sealed class StudentHomeDestinations(
    val route: String,
    val label: String,
    val icon: Int,
) {
    object Home : StudentHomeDestinations(
        route = "home",
        label = "Home",
        icon = R.drawable.baseline_home,
    )

    object Add : StudentHomeDestinations(
        route = "add_log",
        label = "Add Log",
        icon = R.drawable.baseline_add,
    )

    object Update : StudentHomeDestinations(
        route = "update_log?$ADD_SCREEN_ARGUMENT_KEY=" + "{$ADD_SCREEN_ARGUMENT_KEY}",
        label = "Update Log",
        icon = R.drawable.baseline_add,
    ) {
        fun passAttachLogId(attachLogId: String) =
            "update_log?$ADD_SCREEN_ARGUMENT_KEY=$attachLogId"
    }

    object Notifications : StudentHomeDestinations(
        route = "notifications",
        label = "Notifications",
        icon = R.drawable.baseline_notifications_active_24,
    )

    object Account : StudentHomeDestinations(
        route = "account",
        label = "Account",
        icon = R.drawable.baseline_person,
    )
}
