package com.lorraine.domesticjobs.presentation.employer.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.lorraine.domesticjobs.data.model.RequestState
import com.lorraine.domesticjobs.data.repository.JobPostings
import com.lorraine.domesticjobs.presentation.employer.components.HomeAppBar


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EmployerHomeScreen(
    jobs: JobPostings,
    navigateToWrite: () -> Unit,
) {
    var padding by remember { mutableStateOf(PaddingValues()) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            HomeAppBar(
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(
                    end = padding.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = 50.dp
                ),
                onClick = navigateToWrite,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Job Icon",
                    )
                    Text(text = "Job")
                }
            }
        },
        content = {
            padding = it
            when (jobs) {
                is RequestState.Success -> {
                    EmployerHomeContent(
                        paddingValues = it,
                        jobPosting = jobs.data,
                        onClick = {
                            //navigateToWriteWithArgs
                            //Disable onclick
                        },
                    )
                }

                is RequestState.Error -> {
                    EmptyPage(
                        title = "Error",
                        subtitle = "${jobs.error.message}",
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

