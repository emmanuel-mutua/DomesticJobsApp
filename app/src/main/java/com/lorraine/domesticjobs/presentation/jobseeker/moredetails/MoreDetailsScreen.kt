package com.lorraine.domesticjobs.presentation.jobseeker.moredetails

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.lorraine.domesticjobs.data.model.JobPosting
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MoreDetailsScreen(
    jobPosting: JobPosting,
    navigateBack: () -> Unit,
) {
    val viewModel: MoreDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val applicationDetails by viewModel.jobApplicationDetails.collectAsState()
    var isDialogOpen by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Navigate Back"
                        )
                    }
                },
                title = {
                    Text(text = jobPosting.title)
                })
        }
    ) {

        if (uiState.isLoading) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start,
            ) {
                Column {
                    Spacer(modifier = Modifier.height(60.dp))
                    Text(
                        modifier = Modifier.padding(all = 10.dp),
                        text = jobPosting.description,
                        style = TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "Location"
                            )
                            Text(
                                text = " ${jobPosting.nameOfCountry} , ${jobPosting.nameOfCity}",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = FontWeight.Bold
                                ),
                            )
                        }

                        MyClearBox(
                            text = jobPosting.modeOfWork,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MyClearBox(
                            text = "${jobPosting.applicantIds.size} Applicants",
                        )
                        Text(
                            modifier = Modifier.padding(all = 14.dp),
                            text = "${jobPosting.numberOfEmployeesNeeded} Employees Needed",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                color = MaterialTheme.colorScheme.primary
                            ),
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val formatter = remember {
                            DateTimeFormatter.ofPattern(
                                "d MMM uuuu",
                                Locale.getDefault()
                            )
                                .withZone(ZoneId.systemDefault())
                        }
                        Text(
                            modifier = Modifier.padding(all = 2.dp),
                            text = "Application Deadline Date:",
                            style = TextStyle(fontSize = MaterialTheme.typography.bodySmall.fontSize),
                        )
                        Text(
                            text = "Date : ${formatter.format(Instant.ofEpochMilli(jobPosting.applicationDeadline))}",
                            color = MaterialTheme.colorScheme.error,
                            style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize),
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(all = 2.dp),
                        text = "Job Starting Date:",
                        style = TextStyle(fontSize = MaterialTheme.typography.bodySmall.fontSize),
                    )
                    val formatter = remember {
                        DateTimeFormatter.ofPattern("d MMM uuuu", Locale.getDefault())
                            .withZone(ZoneId.systemDefault())
                    }
                    Text(
                        text = "Date : ${formatter.format(Instant.ofEpochMilli(jobPosting.jobStartingDate))}",
                        color = MaterialTheme.colorScheme.primary,
                        style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize),
                    )
                }
                MyClearBox(
                    text = "Salary: ${jobPosting.salary}",
                )
                Column(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Button(
                        onClick = {
                            isDialogOpen = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = "Apply")
                    }
                }
            }
            //Apply Dialog
            if (isDialogOpen) {
                ApplyJobScreen(
                    onApply = {
                        viewModel.setEmployerIdAndJobIdAndTitle(
                            employerId = jobPosting.employerId,
                            jobTitle = jobPosting.title,
                            selectedJobId = jobPosting.title
                        )
                        viewModel.sendApplicationDetails(
                            onSuccess = {
                                viewModel.updateApplicants(jobId = jobPosting.jobId)
                                isDialogOpen = false
                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                            },
                            onError = {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    applicationDetails = applicationDetails,
                    viewModel = viewModel,
                    onDismissRequest = {
                        isDialogOpen = false
                    }
                )
            }
        }
    }
}


@Composable
fun MyClearBox(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = MaterialTheme.typography.titleMedium.fontSize),
        )
    }
}

@Composable
fun MyClearBox2(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                color = MaterialTheme.colorScheme.errorContainer
            ),
        )
    }
}
