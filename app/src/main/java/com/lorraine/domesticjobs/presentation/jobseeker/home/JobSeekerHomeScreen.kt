package com.lorraine.domesticjobs.presentation.jobseeker.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lorraine.domesticjobs.data.model.RequestState
import com.lorraine.domesticjobs.data.repository.JobPostings
import com.lorraine.domesticjobs.presentation.jobseeker.components.JobSeekerTopBar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun JobSeekerHomeScreen(
    jobPostings: JobPostings,
    onJobClick : (String) -> Unit
) {
    var padding by remember { mutableStateOf(PaddingValues()) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .navigationBarsPadding(),

        topBar = {
            JobSeekerTopBar(
                scrollBehavior = scrollBehavior,
            )
        },
        content = {
            padding = it
            when (jobPostings) {
                is RequestState.Success -> {
                    JobSeekerHomeContent(
                        paddingValues = it,
                        jobPosting = jobPostings.data,
                        onJobClick = onJobClick
                    )
                }

                is RequestState.Error -> {
                    EmptyPage(
                        title = "Error",
                        subtitle = "${jobPostings.error.message}",
                    )
                }

                RequestState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                else -> {}
            }
        },
    )
}
