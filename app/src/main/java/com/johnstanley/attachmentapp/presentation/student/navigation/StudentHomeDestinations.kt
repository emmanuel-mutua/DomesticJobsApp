package com.johnstanley.attachmentapp.presentation.student.navigation

import com.johnstanley.attachmentapp.R

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

    object Account : StudentHomeDestinations(
        route = "account",
        label = "Account",
        icon = R.drawable.baseline_person,
    )
}
