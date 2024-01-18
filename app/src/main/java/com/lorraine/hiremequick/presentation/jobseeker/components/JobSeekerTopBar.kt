package com.lorraine.hiremequick.presentation.jobseeker.components
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
import com.lorraine.hiremequick.presentation.jobseeker.home.JobSeekerHomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobSeekerTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val viewModel : JobSeekerHomeViewModel = hiltViewModel()
    TopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {

        },
        title = {
            Text(text = "HireMeQuick", color = MaterialTheme.colorScheme.primary)
        },
        actions = {
            IconButton(onClick = {
                viewModel.getAllJobs()
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
