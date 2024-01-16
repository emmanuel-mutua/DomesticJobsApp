package com.lorraine.hiremequick.presentation.employer.home

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
import androidx.compose.material3.DrawerState
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
import com.lorraine.hiremequick.data.model.RequestState
import com.lorraine.hiremequick.data.repository.JobPostings
import com.lorraine.hiremequick.presentation.employer.components.HomeAppBar
import com.lorraine.hiremequick.presentation.employer.components.NavigationDrawer
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EmployerHomeScreen(
    attachmentLogs: JobPostings,
    drawerState: DrawerState,
    onMenuClicked: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
    navigateToWrite: () -> Unit,
) {
    var padding by remember { mutableStateOf(PaddingValues()) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    NavigationDrawer(drawerState = drawerState, onAddAttachmentDetails = {}) {
        Scaffold(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding(),
            topBar = {
                HomeAppBar(
                    onMenuClicked = onMenuClicked,
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
                when (attachmentLogs) {
                    is RequestState.Success -> {
                        EmployerHomeContent(
                            paddingValues = it,
                            jobPosting = attachmentLogs.data,
                            onClick = navigateToWriteWithArgs,
                        )
                    }

                    is RequestState.Error -> {
                        EmptyPage(
                            title = "Error",
                            subtitle = "${attachmentLogs.error.message}",
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
}
