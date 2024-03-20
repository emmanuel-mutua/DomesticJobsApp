package com.lorraine.domesticjobs.presentation.employer.applications


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lorraine.domesticjobs.presentation.employer.components.HomeAppBar
import com.lorraine.domesticjobs.presentation.jobseeker.moredetails.LoadingScreen


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ApplicationsHomeScreen(
    jobApplicationsUiState: JobApplicationsUiState,
    sendMessage: (String) -> Unit,
    onApplicationEvent: (ApplicationEvent) -> Unit,
    acceptJobSeeker: (String, String) -> Unit,
    declineJobSeeker: (String, String) -> Unit,
    sendEmail: (String) -> Unit,
    call: (String) -> Unit,
) {
    var padding by remember { mutableStateOf(PaddingValues()) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
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
        content = {
            padding = it
            if (jobApplicationsUiState.jobApplications.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 60.dp)
                        .navigationBarsPadding()
                        .padding(top = padding.calculateTopPadding()),
                ) {
                    item {
                        Text(text = "Send Response Email to::")
                        Row(
                            modifier = Modifier
                                .padding(start = 20.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            OutlinedButton(onClick = {
                                onApplicationEvent(
                                    ApplicationEvent.SendAcceptanceEmailEvent(
                                        context
                                    )
                                )
                            }) {
                                Text(text = "All Accepted")
                            }
                            OutlinedButton(onClick = {
                                onApplicationEvent(
                                    ApplicationEvent.SendDeclineEmailEvent(
                                        context
                                    )
                                )
                            }) {
                                Text(text = "All Declined")
                            }
                        }
                        Row(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "No of Applications: ${jobApplicationsUiState.jobApplications.size}",
                                modifier = Modifier.padding(5.dp)
                            )
//                            Button(onClick = { }) {
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ){
//                                    Icon(painter = painterResource(id = R.drawable.download), contentDescription = "Download")
//                                    Text(text = "Applications")
//                                }
//                            }
                        }
                    }
                    items(jobApplicationsUiState.jobApplications) { jobApplicationDetails ->
                        //Applications Holder
                        JobApplicationHolder(
                            jobApplication = jobApplicationDetails,
                            sendEmail = sendEmail,
                            sendMessage = sendMessage,
                            call = call,
                            acceptJobSeeker = acceptJobSeeker,
                            declineJobSeeker = declineJobSeeker
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(horizontal = 5.dp)
                        )
                    }
                }
            } else if (jobApplicationsUiState.isLoading) {
                LoadingScreen()
            } else if (jobApplicationsUiState.isError) {
                ErrorScreen(
                    jobApplicationsUiState.errorMessage
                )
            } else {
                EmptyPage()
            }
        },
    )
}

@Composable
fun EmptyPage(
    title: String = "No applications",
    subtitle: String = "Please wait for one to apply",
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Medium,
            ),
        )
        Text(
            text = subtitle,
            style = TextStyle(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Normal,
            ),
        )
    }
}

@Composable
fun ErrorScreen(
    errorMessage: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = errorMessage,
            style = TextStyle(
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}
