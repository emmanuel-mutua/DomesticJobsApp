package com.lorraine.domesticjobs.presentation.employer.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.lorraine.domesticjobs.presentation.employer.home.EmployerHomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val viewModel : EmployerHomeViewModel = hiltViewModel()

    TopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {

        },
        title = {
            Text(text = "Domestic Jobs Search App", color = MaterialTheme.colorScheme.primary)
        },
        actions = {
                IconButton(onClick = {
viewModel.getJobs()
                }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
        },
    )
}
